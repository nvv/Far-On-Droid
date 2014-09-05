/**
 * Bitcasa Client Android SDK
 * Copyright (C) 2013 Bitcasa, Inc.
 * 215 Castro Street, 2nd Floor
 * Mountain View, CA 94041
 *
 * This file contains an SDK in Java for accessing the Bitcasa infinite drive in Android platform.
 *
 * For support, please send email to support@bitcasa.com.
 */
package com.bitcasa.client.datamodel;

public class AccountInfo {

	private String display_name;
	private String referral_link;
	private String id;
	private long storage_total;
	private long storage_used;
	private String storage_display;
	
	public AccountInfo(){
		
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String displayname) {
		this.display_name = displayname;
	}

	public String getReferralLink() {
		return referral_link;
	}

	public void setReferralLink(String referralLink) {
		this.referral_link = referralLink;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getStorage_total() {
		return storage_total;
	}

	public void setStorage_total(long storage_total) {
		this.storage_total = storage_total;
	}

	public long getStorage_used() {
		return storage_used;
	}

	public void setStorage_used(long storage_used) {
		this.storage_used = storage_used;
	}

	public String getStorage_display() {
		return storage_display;
	}

	public void setStorage_display(String storage_display) {
		this.storage_display = storage_display;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();		
		sb.append("display_name[").append(display_name)
		.append("] referral_link[").append(referral_link)
		.append("] id[").append(id)
		.append("] storage_total[").append(storage_total)
		.append("] storage_used[").append(storage_used)
		.append("] storage_display[").append(storage_display);
		sb.append("]*****");
		
		return sb.toString();
	}
	
	
}
