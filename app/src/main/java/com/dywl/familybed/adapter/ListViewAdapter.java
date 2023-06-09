package com.dywl.familybed.adapter;

import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public abstract class ListViewAdapter<T> extends BaseAdapter {

    private ArrayList<T> mData;
    private int mLayoutRes;           //布局id


    public ListViewAdapter() {
    }

    public ListViewAdapter(ArrayList<T> mData, int mLayoutRes) {
        this.mData = mData;
        this.mLayoutRes = mLayoutRes;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.bind(parent.getContext(), convertView, parent, mLayoutRes
                , position);
        bindView(holder, getItem(position));
        return holder.getItemView();
    }

    public abstract void bindView(ViewHolder holder, T obj);

    //添加一个元素
    public void add(T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    //往特定位置，添加一个元素
    public void add(int position, T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(position, data);
        notifyDataSetChanged();
    }

    public void remove(T data) {
        if (mData != null) {
            mData.remove(data);
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (mData != null) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }


    public static class ViewHolder {

        private SparseArray<View> mViews;   //存储ListView 的 item中的View
        private View item;                  //存放convertView
        private int position;               //游标
        private Context context;            //Context上下文

        //构造方法，完成相关初始化
        private ViewHolder(Context context, ViewGroup parent, int layoutRes) {
            mViews = new SparseArray<>();
            this.context = context;
            View convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
            convertView.setTag(this);
            item = convertView;
        }

        //绑定ViewHolder与item
        public static ViewHolder bind(Context context, View convertView, ViewGroup parent,
                                      int layoutRes, int position) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(context, parent, layoutRes);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.item = convertView;
            }
            holder.position = position;
            return holder;
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getView(int id) {
            T t = (T) mViews.get(id);
            if (t == null) {
                t = (T) item.findViewById(id);
                mViews.put(id, t);
            }
            return t;
        }


        /**
         * 获取当前条目
         */
        public View getItemView() {
            return item;
        }

        /**
         * 获取条目位置
         */
        public int getItemPosition() {
            return position;
        }

        /**
         * 设置文字
         */
        public ViewHolder setText(int id, CharSequence text) {
            View view = getView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            } else if (view instanceof Button) {
                ((Button) view).setText(text);
            }
            return this;
        }

        /**
         * 设置文字
         */
        public ViewHolder setHintText(int id, CharSequence text) {
            View view = getView(id);
            if (view instanceof EditText) {
                ((EditText) view).setHint(text);
            }
            return this;
        }
        public ViewHolder setInputType(int id, int inputType) {
            View view = getView(id);
            if (view instanceof EditText) {
                ((EditText) view).setInputType(inputType);
            }
            return this;
        }

        public ViewHolder setAdapter(int id, ListAdapter adapter) {

            View view = getView(id);
            ListView listView = null;
            if (view instanceof ListView) {
                listView = ((ListView) view);
                listView.setAdapter(adapter);
            }
            int totalHeight = 0;
            int totalWidth = 424;
            for (int i = 0, len = adapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目

                listView.measure(0, 0);  //计算子项View 的宽高
                totalHeight += listView.getMeasuredHeight();  //统计所有子项的总高度
//                totalWidth += listView.getMeasuredWidth()+20;
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
            //listView.getDividerHeight()获取子项间分隔符占用的高度
            //params.height最后得到整个ListView完整显示需要的高度

            params.width = totalWidth;
            listView.setLayoutParams(params);
            return this;
        }

        /**
         * 设置图片
         */
        public ViewHolder setImageResource(int id, int drawableRes) {
            View view = getView(id);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(drawableRes);
            } else {
                view.setBackgroundResource(drawableRes);
            }
            return this;
        }


        /**
         * 设置点击监听
         */
        public ViewHolder setOnClickListener(int id, View.OnClickListener listener) {
            getView(id).setOnClickListener(listener);
            return this;
        }

        /**
         * 设置可见
         */
        public ViewHolder setVisibility(int id, int visible) {

            if (visible == View.GONE) {
                View view = getView(id);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = 0;
                //listView.getDividerHeight()获取子项间分隔符占用的高度
                //params.height最后得到整个ListView完整显示需要的高度

                params.width = 0;
                view.setLayoutParams(params);
            } else {
                getView(id).setVisibility(visible);
            }
            return this;
        }

        /**
         * 设置标签
         */
        public ViewHolder setTag(int id, Object obj) {
            getView(id).setTag(obj);
            return this;
        }
        /**
         * 设置标签
         */
        public ViewHolder setTooltipText(int id, CharSequence text) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getView(id).setTooltipText(text);
            }
            return this;
        }

        /**
         * 设置标签
         */
        public ViewHolder setEnabled(int id, boolean enabled) {
            getView(id).setEnabled(enabled);
            return this;
        }

        //其他方法可自行扩展

    }

}

