# 티스토리를 안드로이드 앱으로 만들기

간단히 티스토리를 웹뷰 형태로 안드로이드 앱으로 만들어 봤습니다.
언어는 자바입니다. 

안드로이드 앱 주소: https://play.google.com/store/apps/details?id=com.taptorestart.blog

앱을 내려받아서 실행해보시면 아실 수 있을 것입니다. 

## 기능

현재는 네 가지 기능을 제공합니다. 
1. 메뉴
2. 홈
3. 검색
4. 공유
입니다. 

서버 없이 새 글이 추가되었을 경우, 새 글 알림이 오도록 했습니다. 

## 다른 블로그 적용 방법

소스코드를 내려받은 뒤
https://github.com/taptorestart/tistory-to-android-app/blob/main/app/src/main/java/com/taptorestart/blog/module/SettingMO.java

com.taptorestart.blog.module 팩키지 안에 
SettingMO.java 파일만 수정하면 됩니다. 

public static String URL_HOME = "https://taptorestart.tistory.com"; 
여기 티스토리 주소만 여러분의 티스토리 블로그 주소로 변경하면 적용됩니다. 

