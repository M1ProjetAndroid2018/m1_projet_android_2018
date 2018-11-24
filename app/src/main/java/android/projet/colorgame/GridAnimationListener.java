package android.projet.colorgame;

import java.util.Random;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

public class GridAnimationListener implements AnimationListener {

	private View animatedView;
	private Context context;
	
	public GridAnimationListener(Context context, View animatedView){
		
		this.animatedView = animatedView.findViewById(R.id.imageView);
		this.context = context;
 	}
	
	public void startAnimation(){
		Random random = new Random();
		int randomDirection=random.nextInt(2);
		Animation anim ;
		if(randomDirection %2==0)
			anim=AnimationUtils.loadAnimation(this.context, R.anim.cell_anim);
		else
			anim=AnimationUtils.loadAnimation(this.context, R.anim.cell_anim_g);
			
		anim.setAnimationListener(this);
		this.animatedView.startAnimation(anim);
		 
	}
	
	public void startAnimationWay(int position){
		Animation anim ;
		int j=position%10;
		int i=(position-j)/10;
		if(j % 2 == 0 && i%2==0 || i%2==1 && j%2==1)
			animatedView.setBackgroundResource(R.drawable.point);
		else
			animatedView.setBackgroundResource(R.drawable.pointtwo);
		
		anim=AnimationUtils.loadAnimation(this.context, R.anim.afficher_chemin);
			
		anim.setAnimationListener(this);
		this.animatedView.startAnimation(anim);
		 
	}
	
	@Override
	public void onAnimationEnd(Animation arg0) {
		this.animatedView.setBackgroundResource(0);
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		
	}

}
