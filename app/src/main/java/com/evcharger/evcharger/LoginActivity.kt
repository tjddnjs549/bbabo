package com.evcharger.evcharger

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.TwitterAuthCredential
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.Arrays.asList

class LoginActivity : AppCompatActivity() {
	var auth: FirebaseAuth? = null //파이어베이스 로그인 관리 클래스
	var googleSignInClient: GoogleSignInClient? = null //구글 로그인 관리 클래스
	var callbackManager: CallbackManager? = null  //페북 로그인 처리 결과 관리 클래스

	val GOOGLE_LOGIN_CODE = 9001


	override fun onCreate(saveInstanceState: Bundle?) {

		super.onCreate(saveInstanceState)
		setContentView(R.layout.activity_login)
		// firebase 로그인 통합 관리하는 object
		auth = FirebaseAuth.getInstance()

		//구글 로그인 옵션
		var gso =
			GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build()
		//구글 로그인 클래스1
		googleSignInClient = GoogleSignIn.getClient(this, gso)
		callbackManager = CallbackManager.Factory.create()

		google_login_btn.setOnClickListener { GoogleLogin() } //구글 로그인 버튼 세팅
		email_login_btn.setOnClickListener { EmailLogin() } // 이메일 로그인 세팅
		facebook_login_btn.setOnClickListener { FacebookLogin() } //페이스북 로그인 버튼 세팅

	}

	// 인증 성공 시 페이지 이동1
	fun MoveMainPage(user: FirebaseUser?) {

		if (user != null) {
			val intent = Intent(this, MainActivity::class.java)
			startActivity(intent)
			finish()
		}
	}

	//구글 로그인 성공 시 토큰값을 credential로 변환 후 fb(파이어베이스)에 넘겨줘 계정 생성
	fun FirebaseAuthWithGoogle(account: GoogleSignInAccount) {

		val credential = GoogleAuthProvider.getCredential(account.idToken, null)

		auth?.signInWithCredential(credential)
			?.addOnCompleteListener(this) { task ->
				progress_bar.visibility = View.GONE
				if (task.isSuccessful) {
					MoveMainPage(auth!!.currentUser)
				}
			}
	}


	//email_edittext,password_edittext 이 널 값인지 확인
	fun EmailLogin() {

		if (email_edittext.text.toString().isNullOrEmpty() || password_edittext.text.toString()
				.isNullOrEmpty()
		) {

			Toast.makeText(this, getString(R.string.signout_fail_null), Toast.LENGTH_SHORT).show()
		} else {
			progress_bar.visibility = View.VISIBLE
			CreateAndLoginEmail()
		}
	}

	//이메일 회원 가입 및 로그인 메서드
	fun CreateAndLoginEmail() {

		auth?.createUserWithEmailAndPassword(
			email_edittext.text.toString(),
			password_edittext.text.toString()
		)
			?.addOnCompleteListener { task ->
				progress_bar.visibility = View.GONE
				if (task.isSuccessful) {
					Toast.makeText(this, getString(R.string.signup_complete), Toast.LENGTH_SHORT)
						.show()
					MoveMainPage(auth?.currentUser)
				} else if (task.exception?.message.isNullOrEmpty()) {
					Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
				} else {
					signinEmail()
				}
			}
	}

	//로그인 메서드
	fun signinEmail() {
		auth?.signInWithEmailAndPassword(
			email_edittext.text.toString(),
			password_edittext.text.toString()
		)
			?.addOnCompleteListener { task ->
				progress_bar.visibility = View.GONE

				if (task.isSuccessful) {

					MoveMainPage(auth?.currentUser)
				} else {
					Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
				}
			}
	}

	// 로그인된 세션 체크 후 자동적으로 페이지 넘어감
	override fun onStart() {
		super.onStart()
		MoveMainPage(auth?.currentUser)
	}
	//로그인이 성공 시 결과값이 넘어오는 함수
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

		super.onActivityResult(requestCode, resultCode, data)

		if (requestCode == GOOGLE_LOGIN_CODE) {
			val task = GoogleSignIn.getSignedInAccountFromIntent(data)
			try { //구글에서 승인된 정보 가지고 오기
				val account = task.getResult(ApiException::class.java)!!
				Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
				FirebaseAuthWithGoogle(account)
			} catch (e: ApiException) {
				Log.w(TAG, "Google sign in failed", e)
			}
		}

		callbackManager?.onActivityResult(requestCode, resultCode, data)
	}

	// 페이스북 로그인 코드
	fun FacebookLogin() {

		progress_bar.visibility = View.VISIBLE

		LoginManager.getInstance()
			.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
		LoginManager.getInstance()
			.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
				override fun onSuccess(loginresult: LoginResult) {
					handleFacebookAccessToken(loginresult.accessToken)
				}

				override fun onCancel() {
					progress_bar.visibility = View.GONE
				}

				override fun onError(error: FacebookException?) {
					progress_bar.visibility = View.GONE
				}
			})
	}

	//페이스북 토큰을 firebase로 넘겨주는 코드
	fun handleFacebookAccessToken(token: AccessToken?) {
		val credential = FacebookAuthProvider.getCredential(token?.token!!)
		auth?.signInWithCredential(credential)
			?.addOnCompleteListener { task ->
				progress_bar.visibility = View.GONE
				if (task.isSuccessful) {
					MoveMainPage(task.result?.user)
				}
			}
	}

	//구글 로그인 코드
	private fun GoogleLogin() {

		progress_bar.visibility = View.VISIBLE
		val signInIntent = googleSignInClient?.signInIntent
		startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
	}

}
