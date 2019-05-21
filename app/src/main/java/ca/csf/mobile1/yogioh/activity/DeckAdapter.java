package ca.csf.mobile1.yogioh.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.csf.mobile1.yogioh.R;
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
        YugiohCard cardForRecyclerView = dataSet.get(position);

        holder.cardId = cardForRecyclerView.id;
        holder.cardDescription = cardForRecyclerView.cardDescription;

        TextView cardName = holder.itemView.findViewById(R.id.cardName);
        cardName.setText(cardForRecyclerView.cardName);

        String type = cardForRecyclerView.type;

        TextView cardType = holder.itemView.findViewById(R.id.cardType);
        cardType.setText(type);

        TextView cardLevel = holder.itemView.findViewById(R.id.cardLevel);
        TextView cardAttackAndDefense = holder.itemView.findViewById(R.id.cardAttackAndDefense);

        if (type.equals(CARD_TYPE_MONSTER))
        {
            cardAttackAndDefense.setText(context.getString(R.string.attack_defense_text, cardForRecyclerView.cardAttack, cardForRecyclerView.cardDefense));
            cardLevel.setText(context.getString(R.string.level_text, cardForRecyclerView.nbStars));
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
        private int cardId;
        private String cardDescription;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(v -> CardDetailActivity.start(itemView.getContext(), Integer.toString(cardId), cardDescription));
        }
    }
}
