package tmon.com.app.command;

import java.util.Date;

import io.vertx.core.json.JsonObject;
import tmon.com.app.db.AuthTable;
import tmon.com.app.db.CountTable;
import tmon.com.app.db.SessionIDTable;
import tmon.com.app.db.UserInfoTable;

public class Auth {	
	
	public static JsonObject signup(JsonObject json) {
		JsonObject packet = json.getJsonObject("packet");
		
		String id = packet.getString("ID");
		String pwd = packet.getString("pwd");
		
		if (AuthTable.inst().get(id) == null) {
			String uidx = CountTable.inst().next("nextUserIndex").toString();
			if (AuthTable.inst().put(id, pwd, uidx)) {
				UserInfoTable.inst().put(uidx, "id", id);
				
				System.out.println("signup true");
				
				return new JsonObject().put("ret", true);
			}
		}
		
		System.out.println("signup false");

		return new JsonObject().put("ret", false).put("message", "이미 존재하는 아이디입니다.");
	}
	
	
	public static JsonObject signin(JsonObject json) {
		JsonObject packet = json.getJsonObject("packet");
		
		String id = packet.getString("ID");
		String pwd = packet.getString("pwd");

		//1) check id exists
		JsonObject userAuthInfo = AuthTable.inst().get(id);
		if (userAuthInfo == null) {
			return new JsonObject().put("ret", false).put("message", "회원이 아닙니다.");
		}

		//2) check a inputed password 
		String userPwd = userAuthInfo.getString("pwd");
		if (pwd.equals(userPwd) == false) {
			return new JsonObject().put("ret", false).put("message", "비밀번호가 다릅니다.");
		}

		String uidx = userAuthInfo.getString("uidx");

		//3)Unlink the old session id
		String oldSessionID = UserInfoTable.inst().get(uidx, "sessionID");
		if (oldSessionID != null) {
			SessionIDTable.inst().del(oldSessionID);
		}
		
		//4) make new session id
		String sessionID = "sid:"+ new Date().getTime() + ":" + uidx;
		
		SessionIDTable.inst().put(sessionID, uidx);
		UserInfoTable.inst().put(uidx, "sessionID", sessionID);
		
		return new JsonObject().put("ret", true).put("sessionID", sessionID);
	}
	
	public static JsonObject signout(JsonObject json) {
		JsonObject session = json.getJsonObject("session");
		String uidx = session.getString("uidx");
		
		UserInfoTable.inst().put(uidx, "sessionID", null);
		SessionIDTable.inst().del(uidx);
		
		return new JsonObject().put("ret", true);
	}
	
	public static JsonObject dropout(JsonObject json) {

		JsonObject session = json.getJsonObject("session");
		String uidx = session.getString("uidx");
		String id = UserInfoTable.inst().get(uidx, "id");
		
		UserInfoTable.inst().del(uidx);
		SessionIDTable.inst().del(uidx);
		AuthTable.inst().del(id);

		return new JsonObject().put("ret", true);
	}
	
}
