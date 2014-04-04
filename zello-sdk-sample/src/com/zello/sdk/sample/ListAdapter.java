package com.zello.sdk.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

	private ArrayList<com.zello.sdk.Contact> _contacts = new ArrayList<com.zello.sdk.Contact>();

	public ListAdapter() {
		super();
	}

	public void setContacts(com.zello.sdk.Contacts contacts) {
		_contacts.clear();
		if (contacts != null) {
			int n = contacts.getCount();
			_contacts.ensureCapacity(n);
			for (int i = 0; i < n; ++i) {
				_contacts.add(contacts.getItem(i));
			}
		}
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return _contacts.isEmpty();
	}

	@Override
	public Object getItem(int position) {
		if (position >= 0 && position < _contacts.size()) {
			return _contacts.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return _contacts.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = ((LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.contact, null);
		}
		com.zello.sdk.Contact contact = null;
		if (position >= 0 && position < _contacts.size()) {
			contact = _contacts.get(position);
		}
		configureView(view, contact);
		return view;
	}

	public static void configureView(View view, com.zello.sdk.Contact contact) {
		boolean show = false;
		ImageView imgContactStatus = (ImageView) view.findViewById(R.id.contact_image);
		TextView txtContactName = (TextView) view.findViewById(R.id.contact_name);
		TextView txtContactStatus = (TextView) view.findViewById(R.id.contact_status);
		if (contact != null) {
			show = true;
			String displayName = contact.getDisplayName(); // Contact name or a full name if not empty
			String statusText = "";
			com.zello.sdk.ContactType type = contact.getType();
			com.zello.sdk.ContactStatus status = contact.getStatus();
			Context context = view.getContext();

			switch (type) {
				case USER:
				case GATEWAY: {
					// User or radio gateway
					String message = contact.getStatusMessage(); // User-defined status message
					statusText = message == null || message.length() == 0 ? statusToText(context, status) : message;
					break;
				}
				case CHANNEL: {
					if (status == com.zello.sdk.ContactStatus.AVAILABLE) {
						int count = contact.getUsersCount();
						String countText = Integer.toString(count);
						statusText = context.getResources().getString(R.string.status_channel_users_count).replace("%count%", countText);
					} else {
						statusText = statusToText(context, status);
					}
					break;
				}
				case GROUP: {
					int count = contact.getUsersCount();
					if (status == com.zello.sdk.ContactStatus.AVAILABLE && count > 0) {
						int total = contact.getUsersTotal();
						String countText = Integer.toString(count);
						String totalText = Integer.toString(total);
						statusText = view.getContext().getResources().getString(R.string.status_group_users_count).replace("%count%", countText).replace("%total%", totalText);
					} else {
						statusText = statusToText(context, com.zello.sdk.ContactStatus.OFFLINE);
					}
					break;
				}
			}

			imgContactStatus.setImageResource(statusToDrawableId(status, type));
			txtContactName.setText(displayName);
			txtContactStatus.setText(statusText);
		}
		imgContactStatus.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		txtContactName.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		txtContactStatus.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	private static String statusToText(Context context, com.zello.sdk.ContactStatus status) {
		int id = R.string.status_offline;
		switch (status) {
			case STANDBY:
				id = R.string.status_standby;
				break;
			case AVAILABLE:
				id = R.string.status_online;
				break;
			case BUSY:
				id = R.string.status_busy;
				break;
			case CONNECTING:
				id = R.string.status_connecting;
				break;
			default:
		}
		return context.getResources().getString(id);
	}

	private static int statusToDrawableId(com.zello.sdk.ContactStatus status, com.zello.sdk.ContactType type) {
		switch (type) {
			case USER: {
				// User
				switch (status) {
					case STANDBY:
						return R.drawable.user_standby;
					case AVAILABLE:
						return R.drawable.user_online;
					case BUSY:
						return R.drawable.user_busy;

				}
				return R.drawable.user_offline;
			}
			case CHANNEL: {
				// Channel
				switch (status) {
					case AVAILABLE:
						return R.drawable.channel_online;

				}
				return R.drawable.channel_offline;
			}
			case GATEWAY: {
				// Radio gateway
				return R.drawable.gateway_online;
			}
			case GROUP: {
				// Group
				return R.drawable.group_online;
			}
		}
		return R.drawable.user_offline;
	}

}
