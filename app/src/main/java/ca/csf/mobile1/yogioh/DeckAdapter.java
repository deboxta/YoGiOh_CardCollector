package ca.csf.mobile1.yogioh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;


public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder>
{
    private final Context context;
    public List<String> dataSet;

    public void setDataSet(List<String> dataSet)
    {
        this.dataSet = dataSet;

        notifyDataSetChanged();
    }

    public DeckAdapter(Context context)
    {
        this.context = context;
        this.dataSet = Collections.emptyList();
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
        String data = dataSet.get(position);
        TextView textView = holder.itemView.findViewById(R.id.textView);
        textView.setText(data);
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
