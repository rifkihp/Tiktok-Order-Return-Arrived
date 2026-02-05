package com.example.tiktokorderreturn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tiktokorderreturn.R;
import com.example.tiktokorderreturn.model.itemListOrder;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter {

	ArrayList<itemListOrder> listDataitemListOrder = new ArrayList<>();
	Context context;

	public ItemListAdapter(Context context, ArrayList<itemListOrder> listDataitemListOrder) {
		this.context = context;
		this.listDataitemListOrder = listDataitemListOrder;
	}

	public void UpdateListitemListOrderAdapter(ArrayList<itemListOrder> listDataitemListOrder) {
		this.listDataitemListOrder = listDataitemListOrder;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listDataitemListOrder.size();
	}

	@Override
	public Object getItem(int position) {
		return listDataitemListOrder.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		public TextView ProdukName;
		public TextView SkuName;
		public int position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {

		final ViewHolder view;
		LayoutInflater inflator =  LayoutInflater.from(parent.getContext());
		if(convertView==null) {
			view = new ViewHolder();
			convertView = inflator.inflate(R.layout.order_item_list, null);

			view.ProdukName = convertView.findViewById(R.id.txtProdukName);
			view.SkuName    = convertView.findViewById(R.id.txtSkuName);

			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}

		final itemListOrder info = listDataitemListOrder.get(position);
		view.position = listDataitemListOrder.indexOf(info);

		view.ProdukName.setText(info.getProduct_name());
		view.SkuName.setText(info.getSku_name());


		return convertView;
	}
}
