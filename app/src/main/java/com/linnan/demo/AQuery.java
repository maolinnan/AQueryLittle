/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.linnan.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.WeakHashMap;

/**
 * 该类是AQuery框架提取出来的工具类
 * @param <T> the generic type
 */
public class AQuery<T extends AQuery<T>> {
    private View root;
    private Activity act;
    private Context context;
    private View view;
    private Object progress;
    private Constructor<T> constructor;
    /**
     * 通过view创建新的AQuery
     * @param view
     * @return
     */
    private T create(View view) {
        AQuery<?> result = null;
        try {
            if (constructor == null){
                constructor = (Constructor<T>) getClass().getConstructor(View.class);
            }
            result = constructor.newInstance(view);
            result.act = act;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) result;
    }

    /**
     * 构造方法
     * @param act
     */
    public AQuery(Activity act) {
        this.act = act;
    }
    public AQuery(View root) {
        this.root = root;
        this.view = root;
    }
    public AQuery(Activity act, View root) {
        this.root = root;
        this.view = root;
        this.act = act;
    }
    public AQuery(Context context) {
        this.context = context;
    }

    /**
     * 找视图
     * @param id
     * @return
     */
    private View findView(int id) {
        View result = null;
        if (root != null) {
            result = root.findViewById(id);
        } else if (act != null) {
            result = act.findViewById(id);
        }
        return result;
    }
    private View findView(String tag) {
        View result = null;
        if (root != null) {
            result = root.findViewWithTag(tag);
        } else if (act != null) {
            View top = ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
            if (top != null) {
                result = top.findViewWithTag(tag);
            }
        }
        return result;
    }
    private View findView(int... path) {
        View result = findView(path[0]);
        for (int i = 1; i < path.length && result != null; i++) {
            result = result.findViewById(path[i]);
        }
        return result;
    }
    public T find(int id) {
        View view = findView(id);
        return create(view);
    }
    /**
     * 找父视图
     * @param id
     * @return
     */
    public T parent(int id) {
        View node = view;
        View result = null;
        while (node != null) {
            if (node.getId() == id) {
                result = node;
                break;
            }
            ViewParent p = node.getParent();
            if (!(p instanceof View)) break;
            node = (View) p;
        }
        return create(result);
    }
    /**
     * 主动释放资源
     * @param root
     * @return
     */
    public T recycle(View root) {
        this.root = root;
        this.view = root;
        this.progress = null;
        this.context = null;
        return self();
    }
    /**
     * 返回自己的AQuery对象
     * @return
     */
    protected T self() {
        return (T) this;
    }
    /**
     * 获取当前自己持有的view
     * @return
     */
    public View getView() {
        return view;
    }
    /**
     * 查视图并返回AQuery对象
     * @param id
     * @return
     */
    public T id(int id) {
        return id(findView(id));
    }
    public T id(View view) {
        this.view = view;
        this.progress = null;
        return self();
    }
    public T id(String tag) {
        return id(findView(tag));
    }
    public T id(int... path) {
        return id(findView(path));
    }

    /**
     * 找progressbar
     * @param id
     * @return
     */
    public T progress(int id) {
        progress = findView(id);
        return self();
    }
    public T progress(Object view) {
        progress = view;
        return self();
    }
    /**
     * 找progressDialog
     * @param dialog
     * @return
     */
    public T progress(Dialog dialog) {
        progress = dialog;
        return self();
    }
    /**
     *给RatingBar设置rating
     * @param rating the rating
     * @return self
     */
    public T rating(float rating) {

        if (view instanceof RatingBar) {
            RatingBar rb = (RatingBar) view;
            rb.setRating(rating);
        }
        return self();
    }
    /**
     * 给TextView设置文字
     * @param resid the resid
     * @return self
     */
    public T text(int resid) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(resid);
        }
        return self();
    }
    public T text(int resid, Object... formatArgs) {
        Context context = getContext();
        if (context != null) {
            CharSequence text = context.getString(resid, formatArgs);
            text(text);
        }
        return self();
    }
    public T text(CharSequence text) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(text);
        }
        return self();
    }
    public T text(CharSequence text, boolean goneIfEmpty) {
        if (goneIfEmpty && (text == null || text.length() == 0)) {
            return gone();
        } else {
            return text(text);
        }
    }
    public T text(Spanned text) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(text);
        }
        return self();
    }
    /**
     *给TextView设置颜色
     * @param color color code in ARGB
     * @return self
     */
    public T textColor(int color) {

        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTextColor(color);
        }
        return self();
    }
    public T textColorId(int id) {

        return textColor(getContext().getResources().getColor(id));
    }


    /**
     *给TextView设置样式
     * @param tf typeface
     * @return self
     */
    public T typeface(Typeface tf) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTypeface(tf);
        }
        return self();
    }
    /**
     * 设置Textview大小
     * @param size size
     * @return self
     */
    public T textSize(float size) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTextSize(size);
        }
        return self();
    }
    /**
     * 设置适配器
     * @param adapter adapter
     * @return self
     */
    public T adapter(Adapter adapter) {
        if (view instanceof AdapterView) {
            AdapterView av = (AdapterView) view;
            av.setAdapter(adapter);
        }
        return self();
    }
    public T adapter(ExpandableListAdapter adapter) {
        if (view instanceof ExpandableListView) {
            ExpandableListView av = (ExpandableListView) view;
            av.setAdapter(adapter);
        }
        return self();
    }
    /**
     * 给ImageVIew设置图片
     * @param resid the resource id
     * @return self
     */
    public T image(int resid) {
        if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            if (resid == 0) {
                iv.setImageBitmap(null);
            } else {
                iv.setImageResource(resid);
            }
        }
        return self();
    }
    public T image(Drawable drawable) {
        if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            iv.setImageDrawable(drawable);
        }
        return self();
    }
    public T image(Bitmap bm) {
        if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            iv.setImageBitmap(bm);
        }
        return self();
    }
    /**
     * 设置标签
     * @param tag
     * @return self
     */
    public T tag(Object tag) {
        if (view != null) {
            view.setTag(tag);
        }
        return self();
    }
    public T tag(int key, Object tag) {
        if (view != null) {
            view.setTag(key, tag);
        }
        return self();
    }
    /**
     * 设置视图的enabled
     * @param enabled state
     * @return self
     */
    public T enabled(boolean enabled) {
        if (view != null) {
            view.setEnabled(enabled);
        }
        return self();
    }
    /**
     * 设置CompoundButton是否选中
     * @param checked state
     * @return self
     */
    public T checked(boolean checked) {
        if (view instanceof CompoundButton) {
            CompoundButton cb = (CompoundButton) view;
            cb.setChecked(checked);
        }
        return self();
    }
    /**
     * 判断CompoundButton是否选中
     * @return checked
     */
    public boolean isChecked() {
        boolean checked = false;
        if (view instanceof CompoundButton) {
            CompoundButton cb = (CompoundButton) view;
            checked = cb.isChecked();
        }
        return checked;
    }
    /**
     * 设置视图是否可以点击
     * @param clickable
     * @return self
     */
    public T clickable(boolean clickable) {
        if (view != null) {
            view.setClickable(clickable);
        }
        return self();
    }


    /**
     * 视图显示相关控制
     * @return self
     */
    public T gone() {
        return visibility(View.GONE);
    }
    public T invisible() {
        return visibility(View.INVISIBLE);
    }
    public T visible() {
        return visibility(View.VISIBLE);
    }
    public T visibility(int visibility) {
        if (view != null && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
        return self();
    }
    /**
     * 设置背景图
     * @param id the id
     * @return self
     */
    public T background(int id) {
        if (view != null) {
            if (id != 0) {
                view.setBackgroundResource(id);
            } else {
                view.setBackgroundDrawable(null);
            }
        }
        return self();
    }
    public T backgroundColor(int color) {
        if (view != null) {
            view.setBackgroundColor(color);
        }
        return self();
    }
    /**
     * 设置背景色
     * @param colorId color code in resource id
     * @return self
     */
    public T backgroundColorId(int colorId) {
        if (view != null) {
            view.setBackgroundColor(getContext().getResources().getColor(colorId));
        }
        return self();
    }
    /**
     * adapter数据改变刷新
     * @return self
     */
    public T dataChanged() {
        if (view instanceof AdapterView) {
            AdapterView<?> av = (AdapterView<?>) view;
            Adapter a = av.getAdapter();
            if (a instanceof BaseAdapter) {
                BaseAdapter ba = (BaseAdapter) a;
                ba.notifyDataSetChanged();
            }
        }
        return self();
    }
    /**
     * 判断当前View是否存在
     * @return true, if is exist
     */
    public boolean isExist() {
        return view != null;
    }
    /**
     * 获取标签
     * @return tag
     */
    public Object getTag() {
        Object result = null;
        if (view != null) {
            result = view.getTag();
        }
        return result;
    }
    public Object getTag(int id) {
        Object result = null;
        if (view != null) {
            result = view.getTag(id);
        }
        return result;
    }

    /**
     * 以下为获取对应视图或元素
     * @return ImageView
     */
    public ImageView getImageView() {
        return (ImageView) view;
    }
    public Gallery getGallery() {
        return (Gallery) view;
    }
    public TextView getTextView() {
        return (TextView) view;
    }
    public EditText getEditText() {
        return (EditText) view;
    }
    public ProgressBar getProgressBar() {
        return (ProgressBar) view;
    }
    public SeekBar getSeekBar() {
        return (SeekBar) view;
    }
    public Button getButton() {
        return (Button) view;
    }
    public CheckBox getCheckBox() {
        return (CheckBox) view;
    }
    public ListView getListView() {
        return (ListView) view;
    }
    public ExpandableListView getExpandableListView() {
        return (ExpandableListView) view;
    }
    public GridView getGridView() {
        return (GridView) view;
    }
    public RatingBar getRatingBar() {
        return (RatingBar) view;
    }
    public WebView getWebView() {
        return (WebView) view;
    }
    public Spinner getSpinner() {
        return (Spinner) view;
    }
    public Editable getEditable() {
        Editable result = null;
        if (view instanceof EditText) {
            result = ((EditText) view).getEditableText();
        }
        return result;
    }
    /**
     * 获取TextView上显示的文字
     * @return the text
     */
    public CharSequence getText() {
        CharSequence result = null;
        if (view instanceof TextView) {
            result = ((TextView) view).getText();
        }
        return result;
    }

    /**
     *获取adapter当前选择的item
     * @return selected
     */
    public Object getSelectedItem() {
        Object result = null;
        if (view instanceof AdapterView<?>) {
            result = ((AdapterView<?>) view).getSelectedItem();
        }
        return result;
    }
    /**
     * 获取adapter当前选择的item位置
     * @return selected position
     */
    public int getSelectedItemPosition() {
        int result = AdapterView.INVALID_POSITION;
        if (view instanceof AdapterView<?>) {
            result = ((AdapterView<?>) view).getSelectedItemPosition();
        }
        return result;
    }
    private static final Class<?>[] ON_CLICK_SIG = {View.class};
    /**
     * 注册点击事件
     * @param listener The callback method.
     * @return self
     */
    public T clicked(OnClickListener listener) {
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return self();
    }
    /**
     * 注册长按事件
     * @param listener The callback method.
     * @return self
     */
    public T longClicked(OnLongClickListener listener) {
        if (view != null) {
            view.setOnLongClickListener(listener);
        }
        return self();
    }
    /**
     * 注册listview的itemClicked事件
     * @param listener The callback method.
     * @return self
     */
    public T itemClicked(OnItemClickListener listener) {
        if (view instanceof AdapterView) {
            AdapterView<?> alv = (AdapterView<?>) view;
            alv.setOnItemClickListener(listener);
        }
        return self();
    }
    /**
     * 为ListView添加滚动事件
     * @param listener
     * @return
     */
    public T scrolled(AbsListView.OnScrollListener listener) {
        if (view instanceof AbsListView) {
            ((AbsListView) view).setOnScrollListener(listener);
        }
        return self();
    }
    /**
     * 注册listview的itemLongClicked事件
     * @param listener The callback method.
     * @return self
     */
    public T itemLongClicked(OnItemLongClickListener listener) {
        if (view instanceof AdapterView) {
            AdapterView<?> alv = (AdapterView<?>) view;
            alv.setOnItemLongClickListener(listener);
        }
        return self();
    }
    /**
     * 注册listview的itemSelected事件
     * @param listener The item selected listener.
     * @return self
     */
    public T itemSelected(OnItemSelectedListener listener) {
        if (view instanceof AdapterView) {
            AdapterView<?> alv = (AdapterView<?>) view;
            alv.setOnItemSelectedListener(listener);
        }
        return self();
    }
    /**
     * 给AdapterView设置选中的item
     * @param position The position of the item to be selected.
     * @return self
     */
    public T setSelection(int position) {
        if (view instanceof AdapterView) {
            AdapterView<?> alv = (AdapterView<?>) view;
            alv.setSelection(position);
        }
        return self();
    }

    /**
     * 清除当前view设置的内容
     * @return self
     */
    public T clear() {
        if (view != null) {
            if (view instanceof ImageView) {
                ImageView iv = ((ImageView) view);
                iv.setImageBitmap(null);
            } else if (view instanceof WebView) {
                WebView wv = ((WebView) view);
                wv.stopLoading();
                wv.clearView();
            } else if (view instanceof TextView) {
                TextView tv = ((TextView) view);
                tv.setText("");
            }
        }
        return self();
    }
    /**
     * 设置view的margin
     * @param leftDip   the left dip
     * @param topDip    the top dip
     * @param rightDip  the right dip
     * @param bottomDip the bottom dip
     * @return self
     */
    public T margin(float leftDip, float topDip, float rightDip, float bottomDip) {
        if (view != null) {
            LayoutParams lp = view.getLayoutParams();
            if (lp instanceof MarginLayoutParams) {
                Context context = getContext();
                int left = dip2px(context, leftDip);
                int top = dip2px(context, topDip);
                int right = dip2px(context, rightDip);
                int bottom = dip2px(context, bottomDip);
                ((MarginLayoutParams) lp).setMargins(left, top, right, bottom);
                view.setLayoutParams(lp);
            }
        }
        return self();
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    /**
     * 设置view的宽
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or ViewGroup.LayoutParams.MATCH_PARENT.
     * @param dip width in dip
     * @return self
     */
    public T width(int dip) {
        size(true, dip, true);
        return self();
    }
    /**
     * 设置view的高
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or ViewGroup.LayoutParams.MATCH_PARENT.
     * @param dip height in dip
     * @return self
     */
    public T height(int dip) {
        size(false, dip, true);
        return self();
    }

    /**
     * 设置view的宽，支持px传递
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or ViewGroup.LayoutParams.MATCH_PARENT.
     * @param width width
     * @param dip   dip or pixel
     * @return self
     */
    public T width(int width, boolean dip) {
        size(true, width, dip);
        return self();
    }
    /**
     * 设置view的高，支持px传递
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or ViewGroup.LayoutParams.MATCH_PARENT.
     * @param height height
     * @param dip    dip or pixel
     * @return self
     */
    public T height(int height, boolean dip) {
        size(false, height, dip);
        return self();
    }
    private void size(boolean width, int n, boolean dip) {
        if (view != null) {
            LayoutParams lp = view.getLayoutParams();
            Context context = getContext();
            if (n > 0 && dip) {
                n = dip2px(context, n);
            }
            if (width) {
                lp.width = n;
            } else {
                lp.height = n;
            }
            view.setLayoutParams(lp);
        }
    }
    /**
     * 返回当前持有的Context
     * @return Context
     */
    public Context getContext() {
        if (act != null) {
            return act;
        }
        if (root != null) {
            return root.getContext();
        }
        return context;
    }

    /**
     * 对当前视图启动动画
     * @param animId Id of the desired animation.
     * @return self
     */
    public T animate(int animId) {
        return animate(animId, null);
    }
    public T animate(int animId, AnimationListener listener) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), animId);
        anim.setAnimationListener(listener);
        return animate(anim);
    }
    public T animate(Animation anim) {
        if (view != null && anim != null) {
            view.startAnimation(anim);
        }
        return self();
    }
    /**
     * 模拟点击事件
     * @return self
     * @see View#performClick()
     */
    public T click() {
        if (view != null) {
            view.performClick();
        }
        return self();
    }
    /**
     * 模拟长按事件
     * @return self
     * @see View#performClick()
     */
    public T longClick() {
        if (view != null) {
            view.performLongClick();
        }
        return self();
    }
    //weak hash map that holds the dialogs so they will never memory leaked
    private static WeakHashMap<Dialog, Void> dialogs = new WeakHashMap<Dialog, Void>();

    /**
     * 显示对话框
     * @return self
     */
    public T show(Dialog dialog) {
        try {
            if (dialog != null) {
                dialog.show();
                dialogs.put(dialog, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return self();
    }
    /**
     * 隐藏对话框
     * @return self
     */
    public T dismiss(Dialog dialog) {
        try {
            if (dialog != null) {
                dialogs.remove(dialog);
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return self();
    }
    /**
     * 隐藏所有对话框
     * @return self
     */
    public T dismiss() {
        Iterator<Dialog> keys = dialogs.keySet().iterator();
        while (keys.hasNext()) {
            Dialog d = keys.next();
            try {
                d.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            keys.remove();
        }
        return self();
    }
    /**
     * 直接查找XML中的视图，并构建view出来
     * @param convertView the view to be reused
     * @param layoutId the desired view type
     * @param root the view root for layout params, can be null
     * @return self
     */
    public static final int TAG_LAYOUT = 0x40FF0003;
    public View inflate(View convertView, int layoutId, ViewGroup root) {
        if (convertView != null) {
            Integer layout = (Integer) convertView.getTag(TAG_LAYOUT);
            if (layout != null && layout.intValue() == layoutId) {
                return convertView;
            }
        }
        LayoutInflater inflater = null;
        if (act != null) {
            inflater = act.getLayoutInflater();
        } else {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        View view = inflater.inflate(layoutId, root, false);
        view.setTag(TAG_LAYOUT, layoutId);
        return view;
    }
    /**
     * 展开、收缩可折叠列表
     * @param position
     * @param expand
     * @return
     */
    public T expand(int position, boolean expand) {
        if (view instanceof ExpandableListView) {
            ExpandableListView elv = (ExpandableListView) view;
            if (expand) {
                elv.expandGroup(position);
            } else {
                elv.collapseGroup(position);
            }
        }
        return self();
    }
    public T expand(boolean expand) {
        if (view instanceof ExpandableListView) {
            ExpandableListView elv = (ExpandableListView) view;
            ExpandableListAdapter ela = elv.getExpandableListAdapter();
            if (ela != null) {
                int count = ela.getGroupCount();
                for (int i = 0; i < count; i++) {
                    if (expand) {
                        elv.expandGroup(i);
                    } else {
                        elv.collapseGroup(i);
                    }
                }
            }
        }
        return self();
    }
}
