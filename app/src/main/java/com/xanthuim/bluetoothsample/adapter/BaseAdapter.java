package com.xanthuim.bluetoothsample.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


/**
 * Created by Xanthium on 2016/10/20.
 * <p>
 * 专门针对有多个ViewType的适配器
 */

public abstract class BaseAdapter<D> extends RecyclerView.Adapter<BaseHolder>
        implements IBaseAdapter<D, BaseHolder>, View.OnClickListener
{
    protected List<D> datas = new ArrayList<>();
    /**
     * 条目点击事件
     */
    protected OnRecyclerViewItemClickListener onItemClickListener;

    @Override
    public final BaseHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView;
        if (getLayoutId(viewType) > 0)
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(getLayoutId(viewType), parent, false);
        } else
        {
            /**
             * 发现使用View.inflater布局显示异常，因此推荐使用LayoutInflater
             */
            itemView = getLayoutView(parent, viewType);
            if (itemView == null)
            {
                throw new IllegalArgumentException("the itemView is null");
            }
        }
        itemView.setOnClickListener(this);
        ButterKnife.bind(this, itemView);
        return createViewHolder(itemView, parent.getContext(), viewType);
    }

    @Override
    public final void onBindViewHolder(BaseHolder holder, int position)
    {
        holder.setData(datas.get(position));
        holder.bindHolder(position);
    }

    @Override
    public int getItemCount()
    {
        if (datas == null || datas.size() == 0)
        {
            return 0;
        } else
        {
            return datas.size();
        }
    }

    @Override
    public void onClick(View v)
    {
        if (onItemClickListener != null)
        {
            onItemClickListener.onItemClick(v, v.getTag());
        }
    }

    /**
     * 设置点击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener)
    {
        this.onItemClickListener = listener;
    }


    /**
     * 设置新的数据，刷新的时候使用
     *
     * @param datas
     */
    public final void setDatas(List<D> datas)
    {
        this.datas = datas;
        notifyDataSetChanged();
    }

    /**
     * 添加新的数据，加载更多时使用
     *
     * @param dataes
     */
    public final void addDatas(List<D> dataes)
    {
        this.datas.addAll(dataes);
        notifyDataSetChanged();
    }

    /**
     * 添加一个元素
     *
     * @param data
     */
    public final void addData(D data)
    {
        this.datas.add(data);
        notifyDataSetChanged();
    }

    /**
     * 获取集合
     *
     * @return
     */
    public List<D> getDatas()
    {
        return datas;
    }

    public void setData(D data)
    {
        this.datas.clear();
        this.datas.add(data);
        notifyDataSetChanged();
    }
}
