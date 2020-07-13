package com.qukan.qkrecordupload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.droidparts.adapter.holder.ViewHolder;
import org.droidparts.annotation.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

public class HorizontalListViewAdapter extends BaseAdapter {
	List<String> mList = new ArrayList<>();
	private Context mContext;
	DisplayImageOptions options;

	public HorizontalListViewAdapter(Context context){
		this.mContext = context;
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.mipmap.ic_launcher)          // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.mipmap.ic_launcher)  // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.mipmap.ic_launcher)       // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中
				.build();
	}

	public void refreshData(List<String> data)
	{
		this.mList.clear();
		this.mList.addAll(data);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mList.size();
	}
	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolderHlv holder;
		if(convertView==null){

			convertView =  LayoutInflater.from( mContext).inflate(R.layout.item_horizontal_list, null);
			holder = new ViewHolderHlv(convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolderHlv)convertView.getTag();
		}
		holder.position = position;

			final String uri = "file://" + mList.get(position);
			ImageLoader.getInstance().displayImage(uri,holder.mImage, options);


		return convertView;
	}

	class ViewHolderHlv extends ViewHolder implements View.OnClickListener {

		private ImageView mImage;

		private int position;

		public ViewHolderHlv(View view) {
			super(view);
			mImage = (ImageView) view.findViewById(R.id.img_list_item);
		}

		@Override
		public void onClick(View v) {

		}
	}
}