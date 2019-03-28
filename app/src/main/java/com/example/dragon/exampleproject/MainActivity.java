package com.example.dragon.exampleproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<MenuData.MenuItemData> menus = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Serializable serializable = getIntent().getSerializableExtra("menu_data");
        if(serializable instanceof ArrayList){
            menus = (ArrayList<MenuData.MenuItemData>)serializable;
        }else{
            menus = new MenuData(MainActivity.class).mainMenuItems;
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new RecyclerView.Adapter<CustomHolder>() {
            @NonNull
            @Override
            public CustomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new CustomHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout,viewGroup,false));
            }

            @Override
            public void onBindViewHolder(@NonNull CustomHolder viewHolder, int i) {
                final MenuData.MenuItemData itemData = menus.get(i);
                viewHolder.textView.setText(itemData.title);
                viewHolder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,itemData.aClass);
                        if(itemData.subMenuItems != null) {
                            intent.putExtra("menu_data", itemData.subMenuItems);
                        }
                        MainActivity.this.startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return menus.size();
            }
        });
    }

    public static class CustomHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.titleView);
        }
    }
}
