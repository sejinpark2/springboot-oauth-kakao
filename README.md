## 진행 과정 <br>
카카오 로그인은 다음과 같이 진행된다. <br>
![image](https://github.com/sejinpark2/springboot-oauth-kakao/assets/141610055/e7b566f0-f0d0-4803-b679-22480fe190ef) <br>

https://developers.kakao.com/ 카카오 디벨로퍼에 접속한다.

내 애플리케이션 -> 애플리케이션 추가하기에 들어간다. <br>
![image](https://github.com/sejinpark2/springboot-oauth-kakao/assets/141610055/55907360-1b60-4639-8a27-baa89aa9ea49) <br>

필요 정보들을 입력하고 저장한 뒤, 앱설정 - 요약 정보에 들어가면 다음과 같은 화면이 보이는데 <br>
이 중 REST_API 키는 사용된다. <br>
![image](https://github.com/sejinpark2/springboot-oauth-kakao/assets/141610055/0433ce70-f95e-4928-984e-a43717f5bd00) <br>

카카오 로그인에 들어가서 카카오 로그인을 활성화 해주고 Redirect URI를 등록해준다 <br>
![image](https://github.com/sejinpark2/springboot-oauth-kakao/assets/141610055/bf1c4c3e-cc2c-4d80-b386-55153de97c23) <br>

## 인가코드 받기
![image](https://github.com/sejinpark2/springboot-oauth-kakao/assets/141610055/afd27650-388b-4fa0-bb6e-728642d0b9b2) <br>

![image](https://github.com/sejinpark2/springboot-oauth-kakao/assets/141610055/7a48de25-e91d-416f-994c-bfceb7df7aed) <br>
REST API 키와 REDIRECT_URI는 아까 발급 받고 등록한걸 넣으면 된다. <br>
위 URI에 접속하고 카카오 로그인을 하면
code= 뒤에 인가 코드를 받을 수 있다.
![image](https://github.com/sejinpark2/springboot-oauth-kakao/assets/141610055/fdffae33-a745-409b-a0b1-8840dc49a88c) <br>

## 토큰 받기








