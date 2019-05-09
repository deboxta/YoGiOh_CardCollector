package ca.csf.mobile1.yogioh.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ca.csf.mobile1.yogioh.R;
import ca.csf.mobile1.yogioh.util.GetCardRessourceFileUtil;

public class RewardActivity extends AppCompatActivity
{

    private Button closeButton;
    private Button openButton;
    private ImageView cardImage;
    private StringBuilder idCard;

    public static void start(Context context)
    {
        Intent rewardPopup = new Intent(context, RewardActivity.class);

        context.startActivity(rewardPopup);
    }

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

        idCard = new StringBuilder();

    }

    private void closeReward(View view)
    {
        cardImage.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, 5));
        finish();
    }

    private void openReward(View view)
    {
        cardImage.setImageResource(GetCardRessourceFileUtil.getCardRessourceFileId(this, 4));
    }

}
