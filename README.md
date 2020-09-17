# OAuth 전체 흐름 정리

### 1. 인증서버 실행 ( port : 8080 )

![image](https://user-images.githubusercontent.com/26649731/93407700-ba8cdf80-f8cd-11ea-9b47-a7a0c449bf71.png)

- 어느 페이지로 이동하던지 일단 로그인을 먼저 해야한다. 이때, 미리 등록해둔 ResourceOwner 계정을 이용하여 로그인한다.

- 인증서버 DB schema

  ```sql
  SELECT * FROM OAUTH_ACCESS_TOKEN ;
  SELECT * FROM OAUTH_APPROVALS  ;
  SELECT * FROM OAUTH_CLIENT_DETAILS  ;
  SELECT * FROM OAUTH_CODE  ;
  SELECT * FROM OAUTH_REFRESH_TOKEN  ;
  SELECT * FROM RESOURCE_OWNER  ;
  ```

  - Resource_Owner : 인증서버 접속시 접근가능하도록 만든 계정.
  - OAUTH_CLIENT_DETAILS : 토큰 발급시 access_token, refresh_token 등을 저장
  - OAUTH_APPROVALS : 어떤 유저가 어떤 클라이언트에서 어떤 범주로 접근했는지, 언제 접근했는지 저장
  - OAUTH_CODE : 프로젝트 인증 방식이 Authorization_code 방식이기 때문에 코드를 발급받는다. 이때 이를 저장

### 2. 클라이언트 계정을 등록해본다.

- create_new_app 클릭

- Name : OAuth Test Application
- Redirect URL : http://localhost:9000/callback
- Type of application : PUBLIC
- 클라이언트 아이디와 비밀번호가 등록되고, DB에도 저장된다. 이것으로 인증서버의 기능은 끝이다. 리스트를 보거나 하는건 DB툴을 이용하면 된다.

![image](https://user-images.githubusercontent.com/26649731/93423539-2a609180-f8f1-11ea-82e3-bd9782a58b8c.png)

### 3. 클라이언트 서버 실행 ( port : 9000 )

- 클라이언트 파일은 무역포탈, 통합포탈의 대리인 역할을 수행하며, 리소스 서버에 접근가능하도록 만들기 허용하는 역할을 수행한다.

- 사전작업 : OAuth2ClientConfig 파일을 수정한다. 

```Java
	/**
	 * 클라이언트 관련 정보 및 인증서버 엔드포인트 설정
	 */
	@Bean
	public OAuth2ProtectedResourceDetails authorizationCode() {
		AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
		
		resourceDetails.setId("resource");
		resourceDetails.setTokenName("oauth_token");
		resourceDetails.setClientId("발급받은거 복사");
		resourceDetails.setClientSecret("발급받은거 복사");
		resourceDetails.setAccessTokenUri("http://localhost:8080/oauth/token");
		resourceDetails.setUserAuthorizationUri("http://localhost:8080/oauth/authorize");
		resourceDetails.setScope(Collections.singletonList("read"));
		resourceDetails.setPreEstablishedRedirectUri("http://localhost:9000/callback");
		resourceDetails.setUseCurrentUri(false);
		resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.header);
		
		return resourceDetails;
	}
```

- 실행한다.

http://localhost:9000/callback?code=5MxOTL&state=xyz

http://localhost:8080/oauth/token?code=7ybVcG&grant_type=authorization_code&scope=read&redirect_uri=http://localhost:9000/callback

```
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MDAyNDU4MjksInVzZXJfbmFtZSI6ImRreW91N0BuYXZlci5jb20iLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiMTI0MDEwMDMtYTBmNS00NjMyLTgxOGYtZjhmNjM0Y2Q1OGI0IiwiY2xpZW50X2lkIjoiZDk5NDRlMTEtZWU3Ni00YmU3LWI4NmMtNzEzNmFjZDAxOWM0Iiwic2NvcGUiOlsicmVhZCJdfQ.SYeW1q4Seisq2fW2mIAJMYz7WQJwS4uFu7Mim2vbEjU",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJka3lvdTdAbmF2ZXIuY29tIiwic2NvcGUiOlsicmVhZCJdLCJhdGkiOiIxMjQwMTAwMy1hMGY1LTQ2MzItODE4Zi1mOGY2MzRjZDU4YjQiLCJleHAiOjE2MDI4MzQyMjksImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiI4Yzk2OWQ2Zi1kZmJhLTQ5Y2YtOGNlNi1iMzEwNWQ3M2I5YjIiLCJjbGllbnRfaWQiOiJkOTk0NGUxMS1lZTc2LTRiZTctYjg2Yy03MTM2YWNkMDE5YzQifQ.XfFiry7ORwBjXLn8B56aEmCfk_Lnfv0aX2acE5f1pTw",
    "expires_in": 3599,
    "scope": "read",
    "jti": "12401003-a0f5-4632-818f-f8f634cd58b4"
}
```





