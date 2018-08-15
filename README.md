# 演示
![gif5新文件 (1).gif](https://upload-images.jianshu.io/upload_images/11678839-b27fcab3cf3b98b7.gif?imageMogr2/auto-orient/strip)
# 使用方式
>①加入SwipeRecycler.java

![无标题.png](https://upload-images.jianshu.io/upload_images/11678839-2c4337b7e375e656.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>②布局
```

    <com.github.pgycode.swiperecycler.SwipeRecycler
        android:layout_width="match_parent"
        android:id="@+id/recycler"
        android:layout_height="match_parent">

    </com.github.pgycode.swiperecycler.SwipeRecycler>
    
```
>③子view布局样例
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="wrap_content"
    android:layout_height="wrap_content">
    <LinearLayout
        android:layout_width="wrap_content"
        android:orientation="horizontal"
        android:layout_height="70dp">
        <!--假设这个textview是主view-->
        <TextView
            android:id="@+id/txt"
            android:layout_width="360dp"
            android:text="测试swipeRecycler"
            android:gravity="center"
            android:textSize="17sp"
            android:textStyle="bold"
            android:textColor="#000000"
            android:background="#ffffff"
            android:layout_height="70dp" />

        <Button
            android:layout_width="90dp"
            android:layout_height="70dp"
            android:gravity="center"
            android:text="置顶"
            android:id="@+id/btn_top"
            android:background="#c8c7c7"
            android:textColor="#ffffff"
            android:textSize="17sp" />

        <Button
            android:layout_width="90dp"
            android:layout_height="70dp"
            android:gravity="center"
            android:text="删除"
            android:id="@+id/btn_del"
            android:background="#e94637"
            android:textColor="#ffffff"
            android:textSize="17sp" />
    </LinearLayout>
    <!--这是分割线-->
    <View
        android:layout_width="match_parent"
        android:layout_height="1px"
        android:background="@color/colorPrimary"/>
</LinearLayout>
```
>④子view的尺寸----这里要分别设置子view与子view内部主view的宽度。
```
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, recycler, false);
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
    layoutParams.width = recycler.getScreenWidth() + recycler.dp2px(180);
    view.setLayoutParams(layoutParams);

    //这里假设这个txt是主view
    View main = view.findViewById(R.id.txt);
    ViewGroup.LayoutParams mainLayoutParams = main.getLayoutParams();
    mainLayoutParams.width = recycler.getScreenWidth();
    main.setLayoutParams(mainLayoutParams);

    return new ViewHolder(view);
}
```
>⑤设置点击事件记得先关闭菜单
```
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        recycler.closeEx();
        Snackbar.make(MainActivity.this.findViewById(R.id.root), position + "", 
           Snackbar.LENGTH_SHORT).show();
    }
});
```
