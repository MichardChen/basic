package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.Member;

public class MemberService {

	public Page<Member> queryMemberListByPage(int page,int size,String mobile,String name,String storeName){
		return Member.dao.queryMemberListByPage(page, size, mobile,name,storeName);
	}

	public Page<Member> queryByPage(int page,int size){
		return Member.dao.queryByPage(page, size);
	}
	
	public Member queryById(int teaId){
		return Member.dao.queryById(teaId);
	}
	
	public boolean updateInfo(Member tea){
		return Member.dao.updateInfo(tea);
	}
	
	public boolean saveInfo(Member tea){
		return Member.dao.saveInfo(tea);
	}
	
	public int updateStatus(int id,String status){
		return Member.dao.updateMemberStatus(id, status);
	}
}
