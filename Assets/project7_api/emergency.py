import os
import pandas as pd
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import numpy as np
import pkg_resources
try:
    pkg_resources.get_distribution('haversine')
except pkg_resources.DistributionNotFound:
    import subprocess
    subprocess.check_call(['pip', 'install', 'haversine'])
from haversine import haversine

import requests
from datetime import datetime, timedelta
import time
import json
from openai import OpenAI

BASE_PATH = './'

def init_openai():
    try:
        with open(os.path.join(BASE_PATH, 'api_key.txt'), 'r') as file:
            api_key = file.read().strip()  # 일반 텍스트로 읽기
        client = OpenAI(api_key=api_key)
        return client
    except Exception as e:
        print(f"OpenAI 초기화 중 오류 발생: {str(e)}")
        return None

def audio_to_text(client,audio_file):
    transcript = client.audio.transcriptions.create(
        model="whisper-1",
        file=audio_file
    )
    return transcript.text


def load_emergency_model():
    try:
        model_dir = os.path.join(BASE_PATH, 'module/model')
        tokenizer = AutoTokenizer.from_pretrained(model_dir)
        model = AutoModelForSequenceClassification.from_pretrained(
            model_dir,
            num_labels=5
        )
        device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
        model.to(device)
        model.eval()
        return tokenizer, model, device
    except Exception as e:
        print(f"모델 로드 중 오류 발생: {str(e)}")
        return None, None, None

def predict_emergency(text, tokenizer, model, device):
    try:
        # text가 딕셔너리인 경우 keywords 추출
        if isinstance(text, dict) and 'keywords' in text:
            input_text = text['keywords']
        else:
            input_text = str(text)  # 문자열이 아닌 경우 변환
            
        inputs = tokenizer(
            input_text,
            return_tensors="pt",
            truncation=True,
            padding=True
        ).to(device)
        
        with torch.no_grad():
            outputs = model(**inputs)
        probabilities = torch.softmax(outputs.logits, dim=1)
        predicted_class = torch.argmax(probabilities, dim=-1).item()
        return predicted_class, probabilities[0]
    except Exception as e:
        print(f"예측 중 오류 발생: {str(e)}")
        return None, None

def summarize_text(client, input_text):
    system_role = '''
    당신은 응급 상황 통화 내용을 분석하고 요약하는 전문 어시스턴트입니다. 다음의 형식으로 응답해주세요:
    응답 형식:
    {
        "summary": "통화 내용 요약",
        "keywords": "증상을 판단할 수 있는 키워드"
    }
    '''
    
    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": system_role},
                {"role": "user", "content": input_text}
            ]
        )
            
        # 응답 내용 출력
        answer = response.choices[0].message.content.strip()
        summary_data = json.loads(answer)
        time.sleep(2)
            
        return {
            "summary": summary_data.get("summary", "요약 없음"),
            "keywords": summary_data.get("keywords", [])
        }
    except json.JSONDecodeError:
        print(f"JSONDecodeError: 응답이 JSON 형식이 아님")
        return None
    except Exception as e:
        print(f"요약 실패: {e}")
        return None

def get_hospital_data():
    return pd.read_csv(os.path.join(BASE_PATH, "C:/Users/User/project7_api/emergency_data.csv"))

def get_dist(start_lat, start_lng, dest_lat, dest_lng):
    #map_key.txt 에 있는 값으로 변경
    client_id = "0pgzeglzjb"
    client_secret = "8YU0QK878AqrGIxplbp6tWUcLwMzfeeEQJNITZMR"
    url = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving"
    headers = {
        "X-NCP-APIGW-API-KEY-ID": client_id,
        "X-NCP-APIGW-API-KEY": client_secret,
    }
    params = {
        "start": f"{start_lng},{start_lat}",
        "goal": f"{dest_lng},{dest_lat}",
        "option": "trafast"
    }
    
    response = requests.get(url, headers=headers, params=params)
    if response.status_code == 200:
        data = response.json()
        try:
            distance_m = data['route']['trafast'][0]['summary']['distance']
            duration_ms = data['route']['trafast'][0]['summary']['duration']
            departure_time = data['route']['trafast'][0]['summary']['departureTime']
            
            distance_km = distance_m / 1000
            duration_seconds = duration_ms // 1000
            hours = duration_seconds // 3600
            minutes = (duration_seconds % 3600) // 60
            seconds = duration_seconds % 60
            
            departure_datetime = datetime.fromisoformat(departure_time)
            arrival_datetime = departure_datetime + timedelta(milliseconds=duration_ms)
            formatted_arrival_time = arrival_datetime.strftime("%Y-%m-%d %H:%M:%S")
            
            return {
                "distance_km": distance_km,
                "duration": f"{hours}시간 {minutes}분 {seconds}초",
                "arrival_time": formatted_arrival_time
            }
        except KeyError:
            print("경로 데이터가 없습니다.")
            return None
    return None
import pandas as pd

def parse_duration(duration_str):
    if duration_str == "정보 없음":
        return float('inf')
    parts = duration_str.split()
    total_seconds = 0
    for part in parts:
        if '시간' in part:
            total_seconds += int(part.split('시간')[0]) * 3600
        elif '분' in part:
            total_seconds += int(part.split('분')[0]) * 60
        elif '초' in part:
            total_seconds += int(part.split('초')[0])
    return total_seconds

def recommend_nearest_hospitals(user_lat, user_lon, hospital_data, top_n=3, alpha=0.05, max_alpha=5.0, alpha_step=0.05):
    try:
        while alpha <= max_alpha:
            min_lat, max_lat = user_lat - alpha, user_lat + alpha
            min_lon, max_lon = user_lon - alpha, user_lon + alpha
            
            filtered_hospitals = hospital_data[
                (hospital_data["위도"] >= min_lat) &
                (hospital_data["위도"] <= max_lat) &
                (hospital_data["경도"] >= min_lon) &
                (hospital_data["경도"] <= max_lon)
            ]
            
            if len(filtered_hospitals) >= top_n:
                distances = []
                for _, row in filtered_hospitals.iterrows():
                    hospital_lat, hospital_lon = row["위도"], row["경도"]
                    distance_data = get_dist(user_lat, user_lon, hospital_lat, hospital_lon)
                    if distance_data:
                        distances.append({
                            "hospital_name": row["병원이름"],
                            "tel1" : row["전화번호 3"],
                            "distance_km": distance_data["distance_km"],
                            "duration": distance_data["duration"],
                            "duration_seconds": parse_duration(distance_data["duration"]),
                            "arrival_time": distance_data["arrival_time"],
                            "address": row["주소"]
                        })
                    else:
                        distances.append({
                            "hospital_name": row["병원이름"],
                            "tel1" : row["전화번호 1"],
                            "distance_km": float('inf'),
                            "duration": "정보 없음",
                            "duration_seconds": float('inf'),
                            "arrival_time": "정보 없음",
                            "address": row["주소"]
                        })
                
                # DataFrame 생성 후 duration 기준으로 정렬
                distance_df = pd.DataFrame(distances)
                sorted_df = distance_df.sort_values("duration_seconds")
                
                # 상위 top_n 병원 반환
                return sorted_df.head(top_n).drop(columns=["duration_seconds"])
            
            alpha += alpha_step
        
        return pd.DataFrame(columns=["hospital_name", "tel1","distance_km", "duration", "arrival_time", "address"])
    
    except Exception as e:
        print(f"병원 추천 중 오류 발생: {str(e)}")
        return pd.DataFrame(columns=["hospital_name", "tel1", "distance_km", "duration", "arrival_time", "address"])


def is_english(text):
    return all(ord(char) < 128 for char in text.replace(' ', '').replace('.', '').replace(',', ''))

def save_hospital_info_by_language(result, text, base_filename=BASE_PATH + 'module/hospital_recommendations'):
    """
    audio_to_text 결과의 언어에 따라 병원 추천 결과를 다른 형식의 CSV 파일로 저장
    """
    # audio_to_text 결과의 언어 확인
    is_eng = is_english(text)
    
    hospitals_data = []
    for hospital in result['nearest_hospitals']:
        # 한국어 시간 형식(0시간 7분 12초)을 영어 형식(0hour 7min 12sec)으로 변환
        duration = hospital['duration']
        if is_eng:
            # 한국어 시간 형식을 영어로 변환
            duration = duration.replace('시간', 'hour').replace('분', 'min').replace('초', 'sec')
            
            hospitals_data.append({
                'Hospital Name': hospital['hospital_name'],
                'tel1': hospital['tel1'],
                'Address': hospital['address'],
                'Distance(km)': hospital['distance_km'],
                'Duration': duration,
                'Expected Arrival': hospital['arrival_time']
            })
        else:
            hospitals_data.append({
                '병원명': hospital['hospital_name'],
                '전화번호1': hospital['tel1'],
                '주소': hospital['address'],
                '거리(km)': hospital['distance_km'],
                '소요시간': hospital['duration'],
                '도착예정시각': hospital['arrival_time']
            })
        
    # DataFrame 생성
    df = pd.DataFrame(hospitals_data)
    
    # 언어에 따라 파일명 결정
    filename = f"{base_filename}_{'en' if is_eng else 'kr'}.csv"
    # CSV 저장
    df.to_csv(filename, index=False, encoding='utf-8-sig')
    print(f"병원 추천 결과가 {filename}에 저장되었습니다.")
    return df
    

def recommend_hospital(text, user_lat, user_lon,top_n):
    # OpenAI 클라이언트 초기화
    client = init_openai()
    
    if not client:
        return None
    
    # 응급 모델 로드
    tokenizer, model, device = load_emergency_model()
    if not all([tokenizer, model, device]):
        return None
    
    #text=audio_to_text(client,audio_file)

    # 텍스트 요약 및 키워드 추출
    summary_result = summarize_text(client, text)
    
    if not summary_result:
        return None
    
    
    # 응급도 예측
    predicted_class, probabilities = predict_emergency(summary_result["keywords"], tokenizer, model, device)
    
    if predicted_class is None:
      return None
    result = None
    if(predicted_class+1 <= 3):
      result = recommend_nearest_hospitals(user_lat, user_lon, get_hospital_data(), top_n)
      result = {
        "summary": summary_result,
        "emergency_class": predicted_class+1,
        "probabilities": probabilities,
        "nearest_hospitals": result.to_dict(orient='records')
      }
        # 사용 예시
      save_hospital_info_by_language(result,text)
      return result
    else:
        return {
            "summary": summary_result,
            "emergency_class": predicted_class + 1,
            "message": "응급상황이 아닙니다."
        }

