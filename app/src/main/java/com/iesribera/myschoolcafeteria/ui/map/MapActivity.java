package com.iesribera.myschoolcafeteria.ui.map;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.iesribera.myschoolcafeteria.R;

public class MapActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_maps);
		BottomNavigationView navView = findViewById(R.id.nav_view);
//		AppBarConfiguration appBarConfiguration =
//				new AppBarConfiguration.Builder(R.id.order,
//												R.id.map,
//												R.id.notification)
//						.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(navView, navController);
	}
}