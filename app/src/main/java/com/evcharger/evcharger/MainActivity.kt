package com.evcharger.evcharger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.evcharger.evcharger.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tool_bar.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

	private lateinit var  binding : ActivityMainBinding
	private lateinit var  homeFragment : HomeFragment

	//메모리에 올라갔을 때
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.contentmain.toolbar.more.setOnClickListener {
			toggleDrawerLayout(binding.root)
		}
		//햄버거 이모티콘 클릭 시 수행
		more.setOnClickListener {
			drawer.openDrawer(GravityCompat.START) // START : left , END : right 랑 같음.
		}

		//네비게이션 메뉴 아이템에 클릭 속성 부여
		binding.navBar.setNavigationItemSelectedListener(this)

		replaceFragment(HomeFragment())

		// 홈 화면 처음에 나오게 하기
		homeFragment = HomeFragment.newInstance()
		supportFragmentManager.beginTransaction().add(R.id.frame_layout,homeFragment).commit()

		// 하단 네비게이션 클릭 시 화면 대체.
		binding.bottomNavigationView.setOnItemSelectedListener {

			when(it.itemId) {
				R.id.home -> replaceFragment(HomeFragment())
				R.id.likelist ->replaceFragment(LikeListFragment())
				R.id.notice ->replaceFragment(NoticeFragment())
				R.id.search ->replaceFragment(SearchFragment())
				R.id.uselist ->replaceFragment(UseListFragment())
				else -> {
				}
			}
			true
		}
	}
	// 화면 대체 메서드
	private fun replaceFragment(fragment: Fragment) {

		val fragmentManager = supportFragmentManager
		val fragmentTransaction = fragmentManager.beginTransaction()
		fragmentTransaction.replace(R.id.frame_layout,fragment) // 메서드 인수로 화면을 대체.
		fragmentTransaction.commit()
	}


	// 네비게이션 메뉴 아이템 클릭 시 수행(조그맣게 나오게 함)
	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.login -> {
				Toast.makeText(applicationContext, "로그인", Toast.LENGTH_SHORT).show()
				replaceFragment(NoticeFragment())
			}
			R.id.nav_notice -> {
				Toast.makeText(applicationContext, "공지사항", Toast.LENGTH_SHORT).show()
				replaceFragment(NoticeFragment())
			}
			R.id.service -> {
				Toast.makeText(applicationContext, "서비스 문의 접수", Toast.LENGTH_SHORT).show()
				replaceFragment(NoticeFragment())
			}
			R.id.manager -> {
				Toast.makeText(applicationContext, "관리자 모드", Toast.LENGTH_SHORT).show()
				replaceFragment(NoticeFragment())
			}
			R.id.languege -> {
				Toast.makeText(applicationContext, "언어 변환", Toast.LENGTH_SHORT).show()
				replaceFragment(NoticeFragment())
			}
		}
		drawer.closeDrawers() // 자동으로 네비게이션 뷰 닫기
		return false
	}
//신유진 바보
	//오른쪽을 터치시 메뉴바 나옴
	private fun toggleDrawerLayout(drawerLayout: DrawerLayout) {

		if(!drawerLayout.isDrawerOpen(GravityCompat.START)) {

		} else {
			drawerLayout.closeDrawer(GravityCompat.START)
		}
	}
	// 백 버튼을 눌렀을 때 수행
	override fun onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) { //네비게이션 뷰 켜져있으면
			drawer.closeDrawers()
		} else {
			super.onBackPressed()
		}
	}

}