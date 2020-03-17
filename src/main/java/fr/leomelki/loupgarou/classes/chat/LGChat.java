package fr.leomelki.loupgarou.classes.chat;

import java.util.HashMap;
import java.util.Map.Entry;

import fr.leomelki.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LGChat {
	@Getter private final HashMap<LGPlayer, LGChatCallback> viewers = new HashMap<LGPlayer, LGChatCallback>();
	@Getter private final LGChatCallback defaultCallback;
	
	public static interface LGChatCallback{
		public String receive(LGPlayer sender, String message);
		public default String send(LGPlayer sender, String message) {return null;};
	}

	public void sendMessage(LGPlayer sender, String message) {
		System.out.println("sendmessage of "+sender.getName()+" "+this);
		String sendMessage = getViewers().get(sender).send(sender, message);
		for(Entry<LGPlayer, LGChatCallback> entry : viewers.entrySet()) {
			System.out.println("   to "+entry.getKey().getName());
			entry.getKey().sendMessage(sendMessage != null ? sendMessage : entry.getValue().receive(sender, message));
		}
	}

	public void join(LGPlayer player, LGChatCallback callback) {
		System.out.println("join "+player.getName()+" ! "+this);
		if(getViewers().containsKey(player))
			getViewers().replace(player, callback);
		else
			getViewers().put(player, callback);
	}
	public void leave(LGPlayer player) {
		System.out.println("leave "+player.getName()+" ! "+this);
		getViewers().remove(player);
	}
}
