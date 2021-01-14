package com.iesribera.myschoolcafeteria.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.iesribera.myschoolcafeteria.R;
import com.iesribera.myschoolcafeteria.models.User;

public class MainActivity extends AppCompatActivity {

	private FirebaseAuth mAuth;
	private GoogleSignInClient mGoogleSignInClient;
	private final int RC_SIGN_IN = 1;
	private final String TAG = "GoogleSignIn";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FirebaseAuth.getInstance().signOut();

		AnimatorSet animatorSet = new AnimatorSet();

		View cafeteriaLogo = findViewById(R.id.item_photo);
		//Traslate
		ObjectAnimator translate = ObjectAnimator.ofFloat(cafeteriaLogo,
														  "translationX",
														  -800, 0);
		translate.setDuration(4000); //duración 4 segundos

		//rotate 360º
		ObjectAnimator rotate = ObjectAnimator.ofFloat(cafeteriaLogo,
													   "rotationX", 0f, 360f);
		rotate.setDuration(4000);
		//show both animations at the same time
		animatorSet.play(translate).with(rotate);
		//start animation
		animatorSet.start();
		SignInButton btnSignIn = findViewById(R.id.signInButton);
		btnSignIn.setOnClickListener(singInClick -> {
			GoogleSignInOptions googleSign = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
					.requestIdToken(getString(R.string.default_web_client_id))
					.requestEmail()
					.build();

			if (mAuth == null || mAuth.getCurrentUser() == null) {
				mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSign);
				mAuth = FirebaseAuth.getInstance();
				signIn();
			}
			if (mAuth != null && mAuth.getCurrentUser() != null) {
				login(btnSignIn);
			}

		});

		rotate.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
			}

			@Override
			public void onAnimationEnd(Animator animator) {
				try {
					Thread.sleep(1000);
				} catch (Exception ignored) {
				}
				if (mAuth != null && mAuth.getCurrentUser() != null) {
					showMainWindow();
				}
			}

			@Override
			public void onAnimationCancel(Animator animator) {
			}

			@Override
			public void onAnimationRepeat(Animator animator) {
			}
		});
	}

	private void login(SignInButton btnSignIn) {
		showMainWindow();
		btnSignIn.setVisibility(View.INVISIBLE);
		Button signOutButton = findViewById(R.id.signOutButton);
		signOutButton.setVisibility(View.VISIBLE);
		signOutButton.setOnClickListener(signOutClick -> {
			mGoogleSignInClient.signOut();
			btnSignIn.setVisibility(View.VISIBLE);
			signOutButton.setVisibility(View.INVISIBLE);
		});
	}

	private void showMainWindow() {
		Intent i = new Intent(getApplicationContext(),
							  MainPanelActivity.class);
		Toast.makeText(getApplicationContext(),
					   "Entrando en su cuenta", Toast.LENGTH_LONG).show();
		startActivity(i);
	}

	private void signIn() {

		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Resultado devuelto al iniciar el Intent de GoogleSignInApi.getSignInIntent (...);
		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			if (task.isSuccessful()) {
				try {
					GoogleSignInAccount account = task.getResult(ApiException.class);
					Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
					Toast.makeText(this, "Login correcto. Accediendo...",
								   Toast.LENGTH_LONG).show();
					firebaseAuthWithGoogle(account.getIdToken());
				} catch (ApiException e) {
					Log.w(TAG, "Google sign in failed", e);
				}
			} else {
				Log.d(TAG, "Error, falló el login:" + task.getException().toString());
				Toast.makeText(this, "Ocurrio un error. " + task.getException().toString(),
							   Toast.LENGTH_LONG).show();
			}
		}
	}

	private void firebaseAuthWithGoogle(String idToken) {
		AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
		mAuth.signInWithCredential(credential)
			 .addOnCompleteListener(this, task -> {
				 if (task.isSuccessful()) {
					 Log.d(TAG, "signInWithCredential:success");
					 FirebaseUser firebaseUser = mAuth.getCurrentUser();
					 User user = User.getInstance();
					 user.setUid(firebaseUser.getUid());
					 user.setName(firebaseUser.getDisplayName());
					 user.setEmail(firebaseUser.getEmail());
					 showMainWindow();
				 } else {
					 Log.w(TAG, "signInWithCredential:failure", task.getException());
				 }
			 });
	}

}