package ca.csf.mobile1.yogioh;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.csf.mobile1.yogioh.activity.CardDetailActivity;
import ca.csf.mobile1.yogioh.model.YugiohCard;


public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder>
{
    private static final String CARD_TYPE_MONSTER = "Monster";
    private final Context context;
    private List<YugiohCard> dataSet;

    public DeckAdapter(Context context, List<YugiohCard> dataSet)
    {
        this.context = context;
        this.dataSet = dataSet;
    }

    public void setDataSet(List<YugiohCard> dataSet)
    {
        this.dataSet = dataSet;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeckAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckAdapter.ViewHolder holder, int position)
    {
        YugiohCard card = dataSet.get(position);

        holder.cardId = card.id;
        holder.cardDescription = card.cardDescription;

        TextView cardName = holder.itemView.findViewById(R.id.cardName);
        cardName.setText(card.cardName);

        String type = card.type;

        TextView cardType = holder.itemView.findViewById(R.id.cardType);
        cardType.setText(type);

        TextView cardLevel = holder.itemView.findViewById(R.id.cardLevel);
        TextView cardAttackAndDefense = holder.itemView.findViewById(R.id.cardAttackAndDefense);

        if (type.equals(CARD_TYPE_MONSTER))
        {
            cardAttackAndDefense.setText(context.getString(R.string.attack_defense_text, card.cardAttack, card.cardDefense));
            cardLevel.setText(context.getString(R.string.level_text, card.nbStars));
        }
        else
        {
            cardLevel.setText("");
            cardAttackAndDefense.setText("");
        }
    }

    @Override
    public int getItemCount()
    {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public int cardId;
        public String cardDescription;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    CardDetailActivity.start(itemView.getContext(), Integer.toString(cardId), cardDescription);
                }
            });
        }
    }
}
