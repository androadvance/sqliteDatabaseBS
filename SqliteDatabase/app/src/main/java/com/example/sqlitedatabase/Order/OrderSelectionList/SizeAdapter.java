package com.example.sqlitedatabase.Order.OrderSelectionList;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.example.sqlitedatabase.R;
import java.util.List;


public class SizeAdapter extends BaseAdapter {

    private Context context;
    private List<SizeList> lists;


    public SizeAdapter(Context context, List<SizeList> lists) {
        this.context = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v = View.inflate(context, R.layout.size_listitem, null);

        TextView tv_size = v.findViewById(R.id.tv_size);
        final EditText eT_qty = v.findViewById(R.id.eT_qty);

        final SizeList ListItem = lists.get(position);

        tv_size.setText(lists.get(position).getSize());
        eT_qty.setText(lists.get(position).getQty());

        eT_qty.setTag(position);

        eT_qty.addTextChangedListener(new CustomTextWatcher(eT_qty));

        return v;
    }

    private class CustomTextWatcher implements TextWatcher {

        private EditText mEditText;

        public CustomTextWatcher(EditText e) {
            mEditText = e;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable editable) {

            int row = 0;

            try {
                String s = mEditText.getTag().toString();
                row = Integer.parseInt(s);
                OrderListItemDetailView.sizeLists.get(row).setQty(editable.toString());
            } catch (Exception er) {
                er.printStackTrace();
            }
        }
    }
}
