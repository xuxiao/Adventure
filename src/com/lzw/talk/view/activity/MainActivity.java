package com.lzw.talk.view.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.lzw.talk.R;
import com.lzw.talk.view.fragment.ContactFragment;
import com.lzw.talk.view.fragment.DiscoverFragment;
import com.lzw.talk.view.fragment.MessageFragment;
import com.lzw.talk.view.fragment.MySpaceFragment;

/**
 * Created by lzw on 14-9-17.
 */
public class MainActivity extends BaseActivity {
  Button messageBtn, contactBtn, discoverBtn, mySpaceBtn;
  View fragmentContainer;
  ContactFragment contactFragment;
  DiscoverFragment discoverFragment;
  MessageFragment messageFragment;
  MySpaceFragment mySpaceFragment;
  public static final int FRAGMENT_N = 4;
  Button[] tabs;
  public static final int[] tabsNormalBackIds = new int[]{R.drawable.tabbar_chat,
      R.drawable.tabbar_contacts, R.drawable.tabbar_discover, R.drawable.tabbar_me};
  public static final int[] tabsActiveBackIds = new int[]{R.drawable.tabbar_chat_active,
      R.drawable.tabbar_contacts_active, R.drawable.tabbar_discover_active,
      R.drawable.tabbar_me_active};
  private Activity ctx;
  View recentTips, contactTips;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ctx = this;
    findView();
    init();
    //mySpaceBtn.performClick();
    contactBtn.performClick();
  }

  private void init() {
    tabs = new Button[]{messageBtn, contactBtn, discoverBtn, mySpaceBtn};
  }

  private void findView() {
    messageBtn = (Button) findViewById(R.id.btn_message);
    contactBtn = (Button) findViewById(R.id.btn_contact);
    discoverBtn = (Button) findViewById(R.id.btn_discover);
    mySpaceBtn = (Button) findViewById(R.id.btn_my_space);
    fragmentContainer = findViewById(R.id.fragment_container);
    recentTips = findViewById(R.id.iv_recent_tips);
    contactTips = findViewById(R.id.iv_contact_tips);
  }

  public void onTabSelect(View v) {
    int id = v.getId();
    FragmentManager manager = getFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();
    hideFragments(transaction);
    setNormalBackgrounds();
    if (id == R.id.btn_message) {
      if (messageFragment == null) {
        messageFragment = new MessageFragment();
        transaction.add(R.id.fragment_container, messageFragment);
      }
      transaction.show(messageFragment);
    } else if (id == R.id.btn_contact) {
      if (contactFragment == null) {
        contactFragment = new ContactFragment();
        transaction.add(R.id.fragment_container, contactFragment);
      }
      transaction.show(contactFragment);
    } else if (id == R.id.btn_discover) {
      if (discoverFragment == null) {
        discoverFragment = new DiscoverFragment();
        transaction.add(R.id.fragment_container, discoverFragment);
      }
      transaction.show(discoverFragment);
    } else if (id == R.id.btn_my_space) {
      if (mySpaceFragment == null) {
        mySpaceFragment = new MySpaceFragment();
        transaction.add(R.id.fragment_container, mySpaceFragment);
      }
      transaction.show(mySpaceFragment);
    }
    int pos;
    for (pos = 0; pos < FRAGMENT_N; pos++) {
      if (tabs[pos] == v) {
        break;
      }
    }
    transaction.commit();
    setTopDrawable(tabs[pos], tabsActiveBackIds[pos]);
  }

  private void setNormalBackgrounds() {
    for (int i = 0; i < tabs.length; i++) {
      Button v = tabs[i];
      setTopDrawable(v, tabsNormalBackIds[i]);
    }
  }

  private void setTopDrawable(Button v, int resId) {
    v.setCompoundDrawablesWithIntrinsicBounds(null, ctx.getResources().getDrawable(resId), null, null);
  }

  private void hideFragments(FragmentTransaction transaction) {
    Fragment[] fragments = new Fragment[]{
        messageFragment, contactFragment,
        discoverFragment, mySpaceFragment
    };
    for (Fragment f : fragments) {
      if (f != null) {
        transaction.hide(f);
      }
    }
  }
}
