package com.ss.education.base;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shcyd.lib.adapter.BasicAdapter;
import com.shcyd.lib.adapter.ViewHolder;
import com.shcyd.lib.widget.NoScrollListView;
import com.ss.education.R;
import com.ss.education.entity.RecordModel;
import com.ss.education.utils.LHFileUtils;
import com.ss.education.utils.SPUtils;
import com.ss.education.utils.voiceutils.MediaManager;
import com.ss.education.utils.voiceutils.VoiceLineUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/22.
 */
public class VoiceListInDetail {
    Context mContext;
    private BasicAdapter adapter;

    // FIXME: 2016/12/9 需要语音暂停播放，请复制以下代码片段
    int pos = -1;//标记当前录音索引//默认没有播放任何一个
    boolean isPlaying;//是否正在播放
    //动画集合
    List<AnimationDrawable> mAnimationDrawables = new ArrayList<>();

    public VoiceListInDetail(Context mContext) {
        this.mContext = mContext;
    }

    //帧动画控制器
    private AnimationDrawable animationDrawable;

    //初始化音频
    public void initVoice(ListView listView, final List<RecordModel> records) {

        adapter = new BasicAdapter<RecordModel>(mContext, records, R.layout.item_add_voice) {

            @Override
            protected void render(final ViewHolder holder, final RecordModel item, final int position) {
                //设置显示时长
                TextView timeTxt = holder.getSubView(R.id.iav_tv_voicetime1);
                timeTxt.setText((int) item.times <= 0 ? 1 + "''" : (int) item.times + "''");

                //判断小红点
                boolean isPlayed = (boolean) SPUtils.get(mContext, item.path, false);
                if (isPlayed) {
                    holder.gone(R.id.iav_iv_red);
                } else {
                    holder.visible(R.id.iav_iv_red);
                }


                //更改录音条长度
                ImageView voiceLine = holder.getSubView(R.id.iclv_iv_voiceLine);
                RelativeLayout.LayoutParams ps = (RelativeLayout.LayoutParams) voiceLine.getLayoutParams();
                ps.width = VoiceLineUtils.getVoiceLineWight(mContext, (int) item.times);
                voiceLine.setLayoutParams(ps); //更改语音长条长度
                //开始设置监听
                voiceLine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //保存小红点标记
                        SPUtils.put(mContext, item.path, true);
                        holder.gone(R.id.iav_iv_red);

                        // FIXME: 2016/12/9 需要语音暂停播放，请复制以下代码片段
                        //****************
                        //判断是否需要停止播放
                        if (pos == position) {
                            if (isPlaying) {
                                isPlaying = false;
                                MediaManager.release();
                                animationDrawable.stop();
                                animationDrawable.selectDrawable(0);//reset
                                return;
                            } else {
                                isPlaying = true;
                            }
                        }


                        //获取语音信号栏，开始播放动画
                        LinearLayout voiceLine = holder.getSubView(R.id.iclv_ll_singer);
                        animationDrawable = (AnimationDrawable) voiceLine.getBackground();
                        mAnimationDrawables.add(animationDrawable);//add list
                        resetAnim();
                        animationDrawable.start();
                        //开始实质播放
                        //记录当前位置正在播放。
                        pos = position;
                        isPlaying = true;
                        //*****************

                        // 播放音频
                        MediaManager.release();//重置
                        String sound;
                        //判断视频来源
                        if (LHFileUtils.isServiceFilesWithHttp(item.path)) {
                            sound = ConnectUrl.FILE_PATH + item.path;
                        } else {
                            sound = item.path;
                        }

                        MediaManager.playSound(sound,
                                new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        animationDrawable.stop();
                                        //// FIXME: 2016/11/24 引用语音的界面，注意添加这段代码。
                                        animationDrawable.selectDrawable(0);//reset
                                        animationDrawable.selectDrawable(0);
                                        pos = -1;
                                    }
                                });
                    }
                });


            }
        };
        listView.setAdapter(adapter);

    }


    public BasicAdapter getAdapter() {
        return adapter;
    }


    //设置语音的长度
    public void setImageLength(View view, int times) {
        LinearLayout.LayoutParams ps = (LinearLayout.LayoutParams) view.getLayoutParams();
        ps.width = VoiceLineUtils.getVoiceLineWight(mContext, times);
        view.setLayoutParams(ps); //更改语音长条长度

    }


    private void resetAnim() {
        for (AnimationDrawable animationDrawable : mAnimationDrawables) {
            animationDrawable.stop();
            animationDrawable.selectDrawable(0);
        }
    }

    public void stopVoice(){
        resetAnim();
        MediaManager.release();
        isPlaying=false;
        pos=-1;
    }

    public void initVoice(NoScrollListView voiceList, List<RecordModel> records, boolean b) {

        adapter = new BasicAdapter<RecordModel>(mContext, records, R.layout.item_add_voice) {

            @Override
            protected void render(final ViewHolder holder, final RecordModel item, final int position) {
                //设置显示时长
                TextView timeTxt = holder.getSubView(R.id.iav_tv_voicetime1);
                timeTxt.setText((int) item.times <= 0 ? 1 + "''" : (int) item.times + "''");

                //判断小红点
                boolean isPlayed = (boolean) SPUtils.get(mContext, item.path, false);
                if (isPlayed) {
                    holder.gone(R.id.iav_iv_red);
                } else {
                    holder.gone(R.id.iav_iv_red);
                }


                //更改录音条长度
                ImageView voiceLine = holder.getSubView(R.id.iclv_iv_voiceLine);
                RelativeLayout.LayoutParams ps = (RelativeLayout.LayoutParams) voiceLine.getLayoutParams();
                ps.width = VoiceLineUtils.getVoiceLineWight(mContext, (int) item.times);
                voiceLine.setLayoutParams(ps); //更改语音长条长度
                //开始设置监听
                voiceLine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //保存小红点标记
                        SPUtils.put(mContext, item.path, true);
                        holder.gone(R.id.iav_iv_red);

                        // FIXME: 2016/12/9 需要语音暂停播放，请复制以下代码片段
                        //****************
                        //判断是否需要停止播放
                        if (pos == position) {
                            if (isPlaying) {
                                isPlaying = false;
                                MediaManager.release();
                                animationDrawable.stop();
                                animationDrawable.selectDrawable(0);//reset
                                return;
                            } else {
                                isPlaying = true;
                            }
                        }


                        //获取语音信号栏，开始播放动画
                        LinearLayout voiceLine = holder.getSubView(R.id.iclv_ll_singer);
                        animationDrawable = (AnimationDrawable) voiceLine.getBackground();
                        mAnimationDrawables.add(animationDrawable);//add list
                        resetAnim();
                        animationDrawable.start();
                        //开始实质播放
                        //记录当前位置正在播放。
                        pos = position;
                        isPlaying = true;
                        //*****************

                        // 播放音频
                        MediaManager.release();//重置
                        String sound;
                        //判断视频来源
                        if (LHFileUtils.isServiceFilesWithHttp(item.path)) {
                            sound = ConnectUrl.FILE_PATH + item.path;
                        } else {
                            sound = item.path;
                        }

                        MediaManager.playSound(sound,
                                new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        animationDrawable.stop();
                                        //// FIXME: 2016/11/24 引用语音的界面，注意添加这段代码。
                                        animationDrawable.selectDrawable(0);//reset
                                        animationDrawable.selectDrawable(0);
                                        pos = -1;
                                    }
                                });
                    }
                });


            }
        };
        voiceList.setAdapter(adapter);

    }
}
