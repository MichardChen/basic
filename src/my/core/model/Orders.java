package my.core.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.sun.jmx.snmp.Timestamp;

import my.core.constants.Constants;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;


@TableBind(table = "mk_order", pk = "id")
public class Orders extends Model<Orders>{
		
		public static final Orders dao = new Orders();
 		public List<Orders> queryOrdersList(String status,int pageSize,int pageNum,String userType,int userId){
			int fromRow = pageSize*(pageNum-1);
			if(StringUtil.equals(status, Constants.ORDER_STATUS.ALL)){
				//所有订单
				if(StringUtil.equals(userType, Constants.USER_TYPE_CD.CLIENT)){
					//客户订单
					return Orders.dao.find("select * from mk_order where member_id="+userId+" order by update_time desc limit "+fromRow+","+pageSize+"");
				}else{
					return Orders.dao.find("select * from mk_order where hair_id="+userId+" or assistant_id="+userId+" order by update_time desc limit "+fromRow+","+pageSize+"");
				}
			}else{
				//非所有订单
				if(StringUtil.equals(userType, Constants.USER_TYPE_CD.CLIENT)){
					return Orders.dao.find("select * from mk_order where member_id="+userId+" and status='"+status+"' order by update_time desc limit "+fromRow+","+pageSize+"");
				}else{
					return Orders.dao.find("select * from mk_order where (hair_id="+userId+" or assistant_id="+userId+") and status='"+status+"' order by update_time desc limit "+fromRow+","+pageSize+"");
				}
			}
 		}
 		
 		public List<Orders> queryBeforeOrdersList(String status,int pageSize,int pageNum,String orderNo){
			int fromRow = pageSize*(pageNum-1);
			if(StringUtil.equals(status, Constants.ORDER_STATUS.ALL)){
				if(StringUtil.isBlank(orderNo)){
					return Orders.dao.find("select * from mk_order order by update_time desc limit "+fromRow+","+pageSize+"");
				}else{
					return Orders.dao.find("select * from mk_order where order_no='"+orderNo+"' order by update_time desc limit "+fromRow+","+pageSize);
				}
			}else{
				if(StringUtil.isBlank(orderNo)){
					return Orders.dao.find("select * from mk_order where status='"+status+"' order by update_time desc limit "+fromRow+","+pageSize);
				}else{
					return Orders.dao.find("select * from mk_order where status='"+status+"' and order_no='"+orderNo+"' order by update_time desc limit "+fromRow+","+pageSize);
				}
			}
 		}
 		
 		public Long queryOrdersListCount(String status,String orderNo){
			if(StringUtil.equals(status, Constants.ORDER_STATUS.ALL)){
				if(StringUtil.isBlank(orderNo)){
					return Db.queryLong("select count(*) from mk_order");
				}else{
					return Db.queryLong("select count(*) from mk_order where order_no='"+orderNo+"'");
				}
			}else{
				if(StringUtil.isBlank(orderNo)){
					return Db.queryLong("select count(*) from mk_order where status='"+status+"'");
				}else{
					return Db.queryLong("select count(*) from mk_order where status='"+status+"' and order_no='"+orderNo+"'");
				}
			}
 		}
 		
 		public Orders queryOrdersByNo(String orderNo){
 			return Orders.dao.findFirst("select * from mk_order where order_no=?",orderNo);
 		} 
 		
 		public Orders queryOrdersById(int id){
 			return Orders.dao.findFirst("select * from mk_order where id=? limit 1",id);
 		} 
 		
 		//更新订单状态
 		public void updateOrder(String orderNo,String status){
 			Db.update("update mk_order set status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"' where order_no="+orderNo);
 		}
 		
 		public Long queryAppointTimes(int userId,String appointTime,String timeTypeCd,int flg,String productTypeCd){
 			//如果是美发产品，1是理发师，0是助理；如果是美容产品，1是美容师，0是理疗师
 			if(StringUtil.equals(productTypeCd, Constants.PRODUCT_TYPE.HAIR_SERIALS)){
 				if(flg == 1){
 					//理发师
 					return Db.queryLong("select count(1) from mk_order where hair_id='"+userId+"' and appoint_time='"+appointTime+"' and appoint_type_cd='"+timeTypeCd+"'");
 				}else{
 					//助理
 					return Db.queryLong("select count(1) from mk_order where assistant_id='"+userId+"' and appoint_time='"+appointTime+"' and appoint_type_cd='"+timeTypeCd+"'");
 				}
 			}else{
 				if(flg == 1){
 					//美容师
 					return Db.queryLong("select count(1) from mk_order where hair_id='"+userId+"' and appoint_time='"+appointTime+"' and appoint_type_cd='"+timeTypeCd+"'");
 				}else{
 					//理疗师
 					return Db.queryLong("select count(1) from mk_order where assistant_id='"+userId+"' and appoint_time='"+appointTime+"' and appoint_type_cd='"+timeTypeCd+"'");
 				}
 			}
 		}
 		
 		public boolean saveOrder(String orderNo,int memberId,int hairId,int assistantId,int storeId,int productId,String appointTime,String timeType,String mark,String status,BigDecimal amount,int hairShuFlg,int assistantShuFlg,BigDecimal hairAmount,BigDecimal assistantAmount,BigDecimal otherFee,int flg){
 			return new Orders().set("order_no", orderNo).set("member_id", memberId).set("hair_id", hairId).set("assistant_id", assistantId).set("store_id", storeId).set("product_id", productId).set("appoint_time", appointTime).set("appoint_type_cd", timeType).set("mark", mark).set("status", status).set("amount", amount).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp()).set("hair_shu_flg", hairShuFlg).set("assistant_shu_flg", assistantShuFlg).set("hair_amount", hairAmount).set("assistant_amount", assistantAmount).set("other_fee", otherFee).set("flg", flg).save();
 		}
 		
 		public int updateOrder(String orderNo,String status,String mark,BigDecimal amount){
 			return Db.update("update mk_order set status='"+status+"',mark='"+mark+"',amount="+amount+",update_time='"+DateUtil.getNowTimestamp()+"' where order_no='"+orderNo+"'");
 		}
 		
 		public List<Orders> queryOrderComment(int adminId){
 			return Orders.dao.find("select distinct mk_order.* from mk_order inner join mk_order_comment where (mk_order.hair_id="+adminId+" or mk_order.assistant_id="+adminId+") and mk_order.status='030007' and mk_order_comment.valid=1 and mk_order_comment.comment is not null order by mk_order.create_time desc limit 5");
 		}
 		
 		public int updateOrderStatus(String orderNo,String status){
 			return Db.update("update mk_order set status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"' where order_no='"+orderNo+"'");
 		}
 		
 		public int updateOrderPay(String orderNo,String status,String payType,String cardNo,BigDecimal realPay){
 			return Db.update("update mk_order set pay_mode_cd='"+payType+"',status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"',card_no='"+cardNo+"',real_amount="+realPay+" where order_no='"+orderNo+"'");
 		}
 		
 		public int updateOrderPayForDiscount(String orderNo,String status,String payType,String cardNo,BigDecimal realPay,BigDecimal amount,BigDecimal hairAmount,BigDecimal assistantAmount,BigDecimal otherAmout){
 			return Db.update("update mk_order set pay_mode_cd='"+payType+"',status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"',card_no='"+cardNo+"',real_amount="+realPay+",amount="+amount+",hair_amount="+hairAmount+",assistant_amount="+assistantAmount+",other_fee="+otherAmout+" where order_no='"+orderNo+"'");
 		}
 		
 		public boolean buy(String orderNo,int memberId,int storeId,int productId,String mark,String status,BigDecimal amount,BigDecimal otherFee,String appointTime,String typeCd,int hairShuFlg,int assistantShuFlg){
 			return new Orders().set("order_no", orderNo).set("member_id", memberId).set("store_id", storeId).set("product_id", productId).set("mark", mark).set("status", status).set("amount", amount).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp()).set("hair_shu_flg", hairShuFlg).set("assistant_shu_flg", assistantShuFlg).set("other_fee", otherFee).set("flg", 2).set("appoint_time", appointTime).set("appoint_type_cd", typeCd).set("hair_shu_flg", hairShuFlg).set("assistant_shu_flg", assistantShuFlg).save();
 		}
 		
 		public int updateComplishTime(String orderNo){
 			return Db.update("update mk_order set complish_time='"+DateUtil.getNowTimestamp()+"' where order_no='"+orderNo+"'");
 		}
}