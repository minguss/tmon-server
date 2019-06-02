package tmon.com.app.command;

import io.vertx.core.json.JsonObject;

public interface JsonFunc {
	JsonObject func(JsonObject param);
}
