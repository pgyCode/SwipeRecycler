package com.github.pgycode.swiperecycler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SwipeRecycler recycler;//工具类
    private List<String> strings;//模拟数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        strings = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            strings.add("test sentence : " + i);
        }

        recycler = findViewById(R.id.recycler);
        recycler.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, recycler, false);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = recycler.getScreenWidth() + recycler.dp2px(180);
                view.setLayoutParams(layoutParams);

                View main = view.findViewById(R.id.txt);
                ViewGroup.LayoutParams mainLayoutParams = main.getLayoutParams();
                mainLayoutParams.width = recycler.getScreenWidth();
                main.setLayoutParams(mainLayoutParams);

                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

                //请注意，为了有良好的体验，请记得每次点击事件生效时，关闭菜单
                //recycler.closeEx();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recycler.closeEx();
                        Snackbar.make(MainActivity.this.findViewById(R.id.root), position + "", Snackbar.LENGTH_SHORT).show();
                    }
                });

                holder.txt.setText(strings.get(position));
                holder.btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recycler.closeEx();
                        //模拟删除
                        strings.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(0, strings.size());
                    }
                });

                holder.btnTop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recycler.closeEx();
                        //模拟置顶
                        String temp = strings.remove(position);
                        strings.add(0, temp);
                        notifyItemChanged(position, 0);
                        notifyItemRangeChanged(0, strings.size());
                    }
                });
            }

            @Override
            public int getItemCount() {
                return strings.size();
            }
        });
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private Button btnDel;
        private Button btnTop;
        private TextView txt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDel = itemView.findViewById(R.id.btn_del);
            btnTop = itemView.findViewById(R.id.btn_top);
            txt = itemView.findViewById(R.id.txt);
        }
    }
}
