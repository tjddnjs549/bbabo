package com.evcharger.evcharger

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class LoginActivity : AppCompatActivity() {

	var auth: FirebaseAuth? = null //fb 관리 클래스
	var googleSignInClient: GoogleSignInClient? = null // 구글 관리 클래스
	var callbackManager: CallbackManager? = null //페이스북 관리 클래스
	val GOOGLE_LOGIN_CODE = 9001 //inter request id

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

		auth = FirebaseAuth.getInstance() // fb(파이어베이스) 로그인 통합 관리하는 프로젝트

		var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(getString(R.string.default_web_client_id))
			.requestEmail()
			.build() // 구글 api키 세팅과 권한 요청 설정.

		googleSignInClient = GoogleSignIn.getClient(this, gso)
		callbackManager = CallbackManager.Factory.create()


		email_login_btn.setOnClickListener { EmailLogin() }
		google_login_btn.setOnClickListener { GoogleLogin() }
		facebook_login_btn.setOnClickListener { FacebookLogin() }
	}

	//구글 로그인 요청 함수
	fun GoogleLogin() {

		progress_bar.visibility = View.GONE //로딩 진행바 표시
		var signIntent = googleSignInClient?.signInIntent
		startActivityForResult(signIntent, GOOGLE_LOGIN_CODE) // dialog창 실행
	}
	//페이스북, 구글 로그인 성공 시 결과 값이 넘어오는 함수
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

		super.onActivityResult(requestCode, resultCode, data)

		if(requestCode == GOOGLE_LOGIN_CODE) {
			var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
				if(result!!.isSuccess){
					var account = result?.signInAccount
					FirebaseAuthWithGoogle(account)
				}
		}
	}
	// 토큰 값을 credential값으로 변환
	fun FirebaseAuthWithGoogle(account: GoogleSignInAccount?){
		var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
		auth?.signInWithCredential(credential)
			?.addOnCompleteListener {
					task ->
				if(task.isSuccessful){
					// 성공 시
					Toast.makeText(this, getString(R.string.signin_complete),Toast.LENGTH_SHORT).show()
					MoveMainPage(task.result?.user)
				}else{
					// 실패 시
					Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
				}
			}
	}

	//페이스북 로그인 함수
	fun FacebookLogin(){
		LoginManager.getInstance()
			.logInWithReadPermissions(this, Arrays.asList("public_profile","email"))
		LoginManager.getInstance()
			.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
				override fun onSuccess(result: LoginResult?) {
					HandleFackbookAccessToken(result?.accessToken) //성공하면
				}
				override fun onCancel() {
				}
				override fun onError(error: FacebookException?) {
				}
			})
	}

	//페북 토근을 fb로 넘겨주는 함수
	fun HandleFackbookAccessToken(token: AccessToken?){
		var credental = FacebookAuthProvider.getCredential(token?.token!!)
		auth?.signInWithCredential(credental)
			?.addOnCompleteListener {
					task ->
				if (task.isSuccessful) {
					// 성공 시
					Toast.makeText(this, getString(R.string.signin_complete),Toast.LENGTH_SHORT).show()
					MoveMainPage(task.result?.user)
				} else {
					// 실패 시
					Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show() }
			}
	}

	//이메일 로그인 함수
	fun EmailLogin() {

		if (email_edittext.text.toString().isNullOrEmpty() || password_edittext.text.toString().isNullOrEmpty()) {
			Toast.makeText(this, getString(R.string.signout_fail_null), Toast.LENGTH_SHORT).show()
		} else {
			progress_bar.visibility = View.VISIBLE
			SigninAndSignup()
		}
	}

	//이메일 회원 가입 및 로그인 함수
	fun SigninAndSignup() {

		auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
			?.addOnCompleteListener {
				task -> progress_bar.visibility = View.GONE //이메일 아이디 생성 코드
				if (task.isSuccessful) {
					//아이디 성공 시
					Toast.makeText(this, getString(R.string.signup_complete),Toast.LENGTH_SHORT).show()
					Toast.makeText(this, getString(R.string.signin_complete),Toast.LENGTH_SHORT).show()
					MoveMainPage(task.result?.user) //다음페이지 호출
				} else if (task.exception?.message.isNullOrEmpty()) {
					//에러 발생 시
					Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
				} else { SigninEmail() }
			}
	}

	//로그인 함수
	fun SigninEmail() {

		auth?.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
			?.addOnCompleteListener {
				task -> progress_bar.visibility = View.GONE
				if (task.isSuccessful) {
					MoveMainPage(task.result?.user)
				} else {
					//로그인 실패 시
					Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
				}
			}
	}

	//메인 페이지로 이동하는 함수
	fun MoveMainPage(user: FirebaseUser?) {
		if (user != null) {
			startActivity(Intent(this, MainActivity::class.java))
		}
	}
}
