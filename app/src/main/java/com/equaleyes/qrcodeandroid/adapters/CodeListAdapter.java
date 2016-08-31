package com.equaleyes.qrcodeandroid.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.equaleyes.qrcodeandroid.BR;
import com.equaleyes.qrcodeandroid.R;
import com.equaleyes.qrcodeandroid.auth.QRAuth;
import com.equaleyes.qrcodeandroid.model.TOTPModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zan on 8/24/16.
 */
public class CodeListAdapter extends RecyclerView.Adapter<CodeListAdapter.CodeViewHolder> {

    private final Context context;

    private List<QRAuth> codes;
    private List<TOTPModel> models;
    private Timer timer;

    public CodeListAdapter(Context context) {
        this.context = context;
        codes = QRAuth.load(context);

        models = new ArrayList<>();
        for (QRAuth code : codes) {
            try {
                models.add(new TOTPModel(code));
            } catch (Exception e) {
                code.delete(context);
                codes.remove(code);
            }
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (TOTPModel model : models) {
                    model.notifyChange();
                }
            }
        }, 0, 1000);
    }

    private void removeItemAt(int position) {
        codes.get(position).delete(context);
        codes.remove(position);
        models.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public CodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding viewBinding = DataBindingUtil.inflate(LayoutInflater
                .from(parent.getContext()), R.layout.code_item_layout, parent, false);
        return new CodeViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(CodeViewHolder holder, int position) {
        ViewDataBinding viewBinding = holder.getBinding();
        viewBinding.setVariable(BR.listItem, models.get(position));
    }

    @Override
    public int getItemCount() {
        return codes.size();
    }


    class CodeViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
        private ViewDataBinding binding;

        public CodeViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setVariable(BR.longClickHandler, this);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        public boolean onLongClick(View view) {
            PopupMenu menu = new PopupMenu(context, binding.getRoot());
            menu.setOnMenuItemClickListener(this);
            menu.inflate(R.menu.code_menu);
            menu.show();
            return true;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.remove_code:
                    removeItemAt(getAdapterPosition());
                    break;
            }

            return true;
        }
    }
}
