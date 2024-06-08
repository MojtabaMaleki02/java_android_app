package com.example.swiftie;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndScreenActivity extends AppCompatActivity {

    TextView textViewPoints;
    Button btnBackToFirstPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the initial and final points from the Intent
        int initialPoints = getIntent().getIntExtra("initialPoints", 0);
        int finalPoints = getIntent().getIntExtra("finalPoints", 0);
        int pointsChange = finalPoints - initialPoints;

        // Load the appropriate layout based on pointsChange
        if (pointsChange > 0) {
            setContentView(R.layout.activity_end_screen);
        } else {
            setContentView(R.layout.lost_end_screen);
        }

        // Initialize the RelativeLayout, textViewPoints, and btnBackToFirstPage in both branches
        RelativeLayout rootLayout = findViewById(R.id.relativeLayoutContainer);
        textViewPoints = findViewById(R.id.textViewPoints);
        btnBackToFirstPage = findViewById(R.id.btnBackToFirstPage);

        // Set initial text to 0
        textViewPoints.setText("0 Points");

        // Scale animation for the entire layout
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.5f, 1.0f,  // Start and end values for the X axis scaling
                0.5f, 1.0f,  // Start and end values for the Y axis scaling
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f  // Pivot point of Y scaling
        );
        scaleAnimation.setDuration(1000); // Animation duration in milliseconds
        scaleAnimation.setFillAfter(true); // Needed to keep the result of the animation
        rootLayout.startAnimation(scaleAnimation);

        // Animate the points change
        animatePointsChange(pointsChange);

        btnBackToFirstPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndScreenActivity.this, FirstPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void animatePointsChange(int pointsChange) {
        ValueAnimator animator = ValueAnimator.ofInt(0, pointsChange);
        animator.setDuration(3000); // 3 seconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                textViewPoints.setText(animatedValue + " Points");
            }
        });
        animator.start();
    }

    // This method can be called to start the EndScreenActivity
    public static void start(Context context, int initialPoints, int finalPoints) {
        Intent intent = new Intent(context, EndScreenActivity.class);
        intent.putExtra("initialPoints", initialPoints);
        intent.putExtra("finalPoints", finalPoints);
        context.startActivity(intent);
    }
}
