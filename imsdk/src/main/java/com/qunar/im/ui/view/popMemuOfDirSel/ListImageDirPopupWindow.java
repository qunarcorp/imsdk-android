package com.qunar.im.ui.view.popMemuOfDirSel;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.CommonAdapter;
import com.qunar.im.ui.adapter.CommonViewHolder;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.structs.ImageFloder;
import com.qunar.im.base.util.Utils;

import java.io.File;
import java.util.List;

public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFloder>
{
	private ListView mListDir;

	public ListImageDirPopupWindow(int width, int height,
			List<ImageFloder> datas, View convertView)
	{
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews()
	{
		mListDir = (ListView) findViewById(R.id.id_list_dir);
		mListDir.setAdapter(new CommonAdapter<ImageFloder>(context, mDatas,
				R.layout.atom_ui_item_popmenu_list_dir)
		{
			@Override
			public void convert(CommonViewHolder helper, ImageFloder item)
			{
				TextView nameTxt = helper.getView(R.id.id_dir_item_name);
				SimpleDraweeView dirImage = helper.getView(R.id.id_dir_item_image);
                TextView countTxt = helper.getView(R.id.id_dir_item_count);
                nameTxt.setText(item.getName());
				FacebookImageUtil.loadLocalImage(new File(item.getFirstImagePath()),dirImage,
						Utils.dipToPixels(context,R.dimen.atom_ui_image_size),
						Utils.dipToPixels(context,R.dimen.atom_ui_image_size));
                countTxt.setText(item.getCount()+"张");
			}
		});
	}

	public interface OnImageDirSelected
	{
		void selected(ImageFloder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
	{
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents()
	{
		mListDir.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

				if (mImageDirSelected != null)
				{
					mImageDirSelected.selected(mDatas.get(position));
				}
			}
		});
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params)
	{
		// TODO Auto-generated method stub
	}

}
