package android.projet.colorgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

public class ColorAdapter extends BaseAdapter {

	private int[] data;
	private int width;
	private LayoutInflater inflater;

	public ColorAdapter(Context context, int[] data, int width) {
		this.data = data;
		this.width = width/10;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong("" + position);
	}

	@Override
	public View getView(int position, View reuseView, ViewGroup group) {

		View view;
		int j=position%10;
		int i=(position-j)/10;
		view =  inflater.inflate(R.layout.color_cell_white, null);


		view.setLayoutParams(new LayoutParams(width, width));
		switch (data[position]) {
			case 0:
				view.findViewById(R.id.imageView).setBackgroundResource(R.drawable.color_blue);
				break;
			case 1:
				view.findViewById(R.id.imageView).setBackgroundResource(R.drawable.color_blue_light);
				break;
			case 2:
				view.findViewById(R.id.imageView).setBackgroundResource(R.drawable.color_pink);
				break;
			case 3:
				view.findViewById(R.id.imageView).setBackgroundResource(R.drawable.color_green);
				break;
			case 4:
				view.findViewById(R.id.imageView).setBackgroundResource(R.drawable.color_cyan);
				break;
			case 5:
				view.findViewById(R.id.imageView).setBackgroundResource(R.drawable.color_red);
				break;
			case 6:
				view.findViewById(R.id.imageView).setBackgroundResource(R.drawable.color_purple);
				break;


		}

		return view;
	}

}
