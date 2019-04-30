package ca.csf.mobile1.yogioh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.csf.mobile1.yogioh.model.CardTypes;
import ca.csf.mobile1.yogioh.model.YugiohCard;


public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder>
{
    public static final String LEVEL_TEXT = "Level : ";
    public static final String ATK_TEXT = "ATK : ";
    public static final String DEF_TEXT = "DEF : ";

    private final Context context;
    public List<YugiohCard> dataSet;

    public void setDataSet(List<YugiohCard> dataSet)
    {
        this.dataSet = dataSet;

        notifyDataSetChanged();
    }

    public DeckAdapter(Context context, List<YugiohCard> dataSet)
    {
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public DeckAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_layout ,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckAdapter.ViewHolder holder, int position)
    {
        TextView cardName = holder.itemView.findViewById(R.id.cardName);
        cardName.setText(dataSet.get(position).cardName);

        String type = dataSet.get(position).type;

        TextView cardType = holder.itemView.findViewById(R.id.cardType);
        cardType.setText(type);

        TextView cardLevel = holder.itemView.findViewById(R.id.cardLevel);
        TextView cardAttackAndDefense = holder.itemView.findViewById(R.id.cardAttackAndDefense);

        if (type.equals(CardTypes.Monster))
        {
            cardLevel.setText(LEVEL_TEXT + dataSet.get(position).nbStars);
            cardAttackAndDefense.setText(ATK_TEXT + dataSet.get(position).cardAttack + DEF_TEXT + dataSet.get(position).cardDefense);
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
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }
    }
}
