package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_invoice_gettearecord", pk = "id")
public class InvoiceGetteaRecord extends Model<InvoiceGetteaRecord> {

	public static final InvoiceGetteaRecord dao = new InvoiceGetteaRecord();
	
	public int saveInfos(InvoiceGetteaRecord data){
		InvoiceGetteaRecord d = new InvoiceGetteaRecord().setAttrs(data);
		d.save();
		return d.getInt("id");
	}
}
