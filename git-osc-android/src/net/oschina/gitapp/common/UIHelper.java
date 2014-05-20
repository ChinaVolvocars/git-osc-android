package net.oschina.gitapp.common;

import java.util.regex.Pattern;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppManager;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Event;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Toast;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper {
	private final static String TAG = "UIHelper";
	
	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;

	public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
	public final static int LISTVIEW_DATATYPE_BLOG = 0x02;
	public final static int LISTVIEW_DATATYPE_POST = 0x03;
	public final static int LISTVIEW_DATATYPE_TWEET = 0x04;
	public final static int LISTVIEW_DATATYPE_ACTIVE = 0x05;
	public final static int LISTVIEW_DATATYPE_MESSAGE = 0x06;
	public final static int LISTVIEW_DATATYPE_COMMENT = 0x07;

	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;

	/** 表情图片匹配 */
	private static Pattern facePattern = Pattern
			.compile("\\[{1}([0-9]\\d*)\\]{1}");

	/** 全局web样式 */
	// 链接样式文件，代码块高亮的处理
	public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
			+ "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
			+ "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>";
	public final static String WEB_STYLE = linkCss + "<style>* {font-size:14px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;overflow: auto;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //模拟器
						i.setType("message/rfc822"); // 真机
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "zhangdeyi@oschina.net" });
						i.putExtra(Intent.EXTRA_SUBJECT,
								"开源中国Android客户端 - 错误报告");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "发送错误报告"));
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.show();
	}
	
	/**
	 * 点击返回监听事件
	 * 
	 * @param activity
	 * @return
	 */
	public static View.OnClickListener finish(final Activity activity) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		};
	}
	
	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}
	
	/**
	 * 分析并组合动态的标题
	 * @param author_name 动态作者的名称
	 * @param pAuthor_And_pName 项目的作者和项目名
	 * @param eventTitle 事件的title（Issue或者pr或分支）
	 * @return
	 */
	public static SpannableString parseEventTitle(String author_name, 
			String pAuthor_And_pName, String eventTitle,Event event) {
		String title = "";
		int action = event.getAction();
		
		switch (action) {
		case Event.EVENT_TYPE_CREATED:// 创建了issue
			title = "在项目" + pAuthor_And_pName + "创建了" + eventTitle + event.getTarget_id();
			break;
		case Event.EVENT_TYPE_UPDATED:// 更新项目
			title = "更新了项目" + pAuthor_And_pName;
			break;
		case Event.EVENT_TYPE_CLOSED:// 关闭项目
			title = "关闭了项目" + pAuthor_And_pName;
			break;
		case Event.EVENT_TYPE_REOPENED:// 重新打开了项目
			title = "重新打开了项目" + pAuthor_And_pName;
			break;
		case Event.EVENT_TYPE_PUSHED:// push
			title = "推送到了项目" + pAuthor_And_pName + "的" + event.getData().getRef();
			break;
		case Event.EVENT_TYPE_COMMENTED:// 评论
			title = "评论了项目" + pAuthor_And_pName + "的" + eventTitle;
			break;
		case Event.EVENT_TYPE_MERGED:// 合并
			title = "接受了项目" + pAuthor_And_pName + "的" + eventTitle + event.getTarget_id();
			break;
		case Event.EVENT_TYPE_JOINED://# User joined project
			title = "加入了项目" + pAuthor_And_pName;
			break;
		case Event.EVENT_TYPE_LEFT://# User left project
			title = "离开了项目" + pAuthor_And_pName;
			break;
		case Event.EVENT_TYPE_FORKED:// fork了项目
			title = "Fork了项目" + pAuthor_And_pName;
			break;
		default:
			title = "更新了动态：";
			break;
		}
		title = author_name + " " + title;
		SpannableString sps = new SpannableString(title);
		
		// 设置用户名字体大小、加粗、高亮
		sps.setSpan(new AbsoluteSizeSpan(14, true), 0, author_name.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sps.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				author_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sps.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 0,
				author_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		// 设置项目名字体大小和高亮
		int start = title.indexOf(pAuthor_And_pName);
		int end = start + pAuthor_And_pName.length();
		sps.setSpan(new AbsoluteSizeSpan(14, true), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sps.setSpan(
				new ForegroundColorSpan(Color.parseColor("#0e5986")),
				start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		// 设置动态的title字体大小和高亮
		if (!StringUtils.isEmpty(eventTitle)) {
			start = title.indexOf(eventTitle);
			end = start + eventTitle.length();
			sps.setSpan(new AbsoluteSizeSpan(14, true), start, end,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			sps.setSpan(
					new ForegroundColorSpan(Color.parseColor("#0e5986")),
					start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return sps;
	}
}