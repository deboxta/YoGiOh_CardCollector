package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ca.csf.mobile1.yogioh.R;

public class RewardActivity extends AppCompatActivity
{

    private Button closeButton;
    private Button openButton;
    private ImageView cardImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_popup);

        cardImage = findViewById(R.id.rewardImageView);
        openButton = findViewById(R.id.buttonOpen);
        closeButton = findViewById(R.id.buttonClose);

        cardImage.setImageResource(R.drawable.backside_card);

        openButton.setOnClickListener(this::openReward);
        closeButton.setOnClickListener(this::closeReward);
    }

    private void closeReward(View view)
    {
        finish();
    }

    private void openReward(View view)
    {
        cardImage.setImageResource(R.drawable.card2);

    }

}
