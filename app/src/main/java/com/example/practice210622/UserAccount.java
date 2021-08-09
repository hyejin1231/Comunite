package com.example.practice210622;

// 사용자 계정 정보 모델 클래스
public class UserAccount {
    private String et_email;
    private String et_pwd;
    private String idToken; // firebase uid(고유정보)
    private String profile;
    private String nickName;
    private String kakao;


    public UserAccount() { } //firebase사용할때는 빈 생성자 꼭 만들어야한다고 함!

    public String getEt_email() {
        return et_email;
    }

    public void setEt_email(String et_email) {
        this.et_email = et_email;
    }

    public String getEt_pwd() {
        return et_pwd;
    }

    public void setEt_pwd(String et_pwd) {
        this.et_pwd = et_pwd;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getKakao() {
        return kakao;
    }

    public void setKakao(String kakao) {
        this.kakao = kakao;
    }
}
