package ca.csf.mobile1.yogioh;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.csf.mobile1.yogioh.activity.ExchangeActivity;
import ca.csf.mobile1.yogioh.model.CardTypes;
import ca.csf.mobile1.yogioh.model.YugiohCard;


public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder>
{
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

        TextView cardName = holder.itemView.findViewById(R.id.cardName);
        cardName.setText(card.cardName);

        String type = card.type;

        TextView cardType = holder.itemView.findViewById(R.id.cardType);
        cardType.setText(type);

        TextView cardLevel = holder.itemView.findViewById(R.id.cardLevel);
        TextView cardAttackAndDefense = holder.itemView.findViewById(R.id.cardAttackAndDefense);

        if (type.equals(CardTypes.Monster.toString()))
        {
            StringBuilder attackAndDefenseBuilder = new StringBuilder();
            attackAndDefenseBuilder.append(context.getString(R.string.atk_text));
            attackAndDefenseBuilder.append(card.cardAttack);
            attackAndDefenseBuilder.append("\t");
            attackAndDefenseBuilder.append(context.getString(R.string.def_text));
            attackAndDefenseBuilder.append(card.cardDefense);

            StringBuilder levelBuilder = new StringBuilder();
            levelBuilder.append(context.getString(R.string.level_text));
            levelBuilder.append(card.nbStars);

            cardLevel.setText(levelBuilder.toString());
            cardAttackAndDefense.setText(attackAndDefenseBuilder.toString());
        }
        else
        {
            cardLevel.setText("");
            cardAttackAndDefense.setText("");
        }
    }

    public int getCardId(int position)
    {
        return dataSet.get(position).id;
    }

    @Override
    public int getItemCount()
    {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    Log.i("Click", "Item " + getAdapterPosition() + " clicked");

                }
            });
        }
    }
}
