/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.model.actor.instance;

import java.util.logging.Logger;

import javolution.util.FastMap;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.NpcBufferTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Multisell;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExHeroList;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.templates.chars.L2NpcTemplate;
import net.sf.l2j.util.L2FastList;

/**
 * Olympiad Npc's Instance
 *
 * @author godson
 */

public class L2OlympiadManagerInstance extends L2NpcInstance
{
	private static Logger _logOlymp = Logger.getLogger(L2OlympiadManagerInstance.class.getName());

	private static final int GATE_PASS = Config.ALT_OLY_COMP_RITEM;

	public L2OlympiadManagerInstance (int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback (L2PcInstance player, String command)
	{
		int npcId = getNpcId();

		if (command.startsWith("OlympiadDesc"))
		{
			int val = Integer.parseInt(command.substring(13,14));
			String suffix = command.substring(14);
			showChatWindow(player, val, suffix);
		}
		else if (command.startsWith("OlympiadNoble"))
		{
			if (!player.isNoble() || player.getClassId().level() < 3)
				return;

			int val = Integer.parseInt(command.substring(14));
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

			switch(val)
			{
				case 1:
					Olympiad.getInstance().unRegisterNoble(player);
					break;
				case 2:
					int classed = 0;
					int nonClassed = 0;
					int[] array = Olympiad.getInstance().getWaitingList();

					if (array != null)
					{
						classed = array[0];
						nonClassed = array[1];
					}
					html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "noble_registered.htm");
					html.replace("%listClassed%", String.valueOf(classed < 100 ? "Fewer than 100" : "More than 100"));
					html.replace("%listNonClassedTeam%", String.valueOf("Fewer than 100"));
					html.replace("%listNonClassed%", String.valueOf(nonClassed < 100 ? "Fewer than 100" : "More than 100"));
					html.replace("%objectId%", String.valueOf(getObjectId()));
					player.sendPacket(html);
					break;
				case 3:
					int points = Olympiad.getInstance().getNoblePoints(player.getObjectId());
					html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "noble_points1.htm");
					html.replace("%points%", String.valueOf(points));
					html.replace("%objectId%", String.valueOf(getObjectId()));
					player.sendPacket(html);
					break;
				case 4:
					Olympiad.getInstance().registerNoble(player, false);
					break;
				case 5:
					Olympiad.getInstance().registerNoble(player, true);
					break;
				case 6:
					int passes = Olympiad.getInstance().getNoblessePasses(player.getObjectId());
					if (passes > 0)
					{
						L2ItemInstance item = player.getInventory().addItem("Olympiad", GATE_PASS, passes, player, this);

						InventoryUpdate iu = new InventoryUpdate();
						iu.addModifiedItem(item);
						player.sendPacket(iu);

						SystemMessage sm = new SystemMessage(SystemMessageId.EARNED_ITEM);
						sm.addItemNumber(passes);
						sm.addItemName(item);
						player.sendPacket(sm);
					}
					else
					{
						html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "noble_nopoints.htm");
						html.replace("%objectId%", String.valueOf(getObjectId()));
						player.sendPacket(html);
					}
					break;
				case 7:
					L2Multisell.getInstance().separateAndSend(102, player, false, getCastle().getTaxRate());
					break;
				case 9:
					L2Multisell.getInstance().separateAndSend(103, player, false, getCastle().getTaxRate());
					break;
				case 8:
					int point = Olympiad.getInstance().getNoblePoints(player.getObjectId());
					html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "noble_points2.htm");
					html.replace("%points%", String.valueOf(point));
					html.replace("%objectId%", String.valueOf(getObjectId()));
					player.sendPacket(html);
					break;
				default:
					_logOlymp.warning("Olympiad System: Couldnt send packet for request " + val);
				break;
			}
		}
		else if (command.startsWith("OlyBuff"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			String[] params = command.split(" ");

			if (params[1] == null)
			{
				_log.warning("Olympiad Buffer Warning: npcId = " + npcId + " has no buffGroup set in the bypass for the buff selected.");
				return;
			}
			int buffGroup = Integer.parseInt(params[1]);

			int[] npcBuffGroupInfo = NpcBufferTable.getInstance().getSkillInfo(npcId, buffGroup);

			if (npcBuffGroupInfo == null)
			{
				_log.warning("Olympiad Buffer Warning: npcId = " + npcId + " Location: " + getX() + ", " + getY() + ", " + getZ() + " Player: " + player.getName() + " has tried to use skill group (" + buffGroup + ") not assigned to the NPC Buffer!");
				return;
			}

			int skillId = npcBuffGroupInfo[0];
			int skillLevel = npcBuffGroupInfo[1];

			L2Skill skill = SkillTable.getInstance().getInfo(skillId,skillLevel);

			if (player.olyBuff > 0)
			{
				if (skill != null)
				{
					skill.getEffects(player, player);
					player.olyBuff--;
				}
			}

			if (player.olyBuff > 0)
			{
				html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "olympiad_buffs.htm");
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
			} 
			else
			{
				html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "olympiad_nobuffs.htm");
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
				this.deleteMe();                    	
			}
		} else if (command.startsWith("Olympiad"))
		{
			int val = Integer.parseInt(command.substring(9,10));

			NpcHtmlMessage reply = new NpcHtmlMessage(getObjectId());

			switch (val)
			{
				case 1:
					FastMap<Integer, String> matches = Olympiad.getInstance().getMatchList();
					reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "olympiad_observe.htm");

					for (int i = 0; i < Olympiad.getStadiumCount(); i++) {
						int arenaID = i + 1;

						// &$906; -> \\&\\$906;
						reply.replace("%title"+arenaID+"%", matches.containsKey(i) ? matches.get(i) : "\\&\\$906;");
					}
					reply.replace("%objectId%", String.valueOf(getObjectId()));
					player.sendPacket(reply);
					break;
				case 2:
					// for example >> Olympiad 1_88
					int classId = Integer.parseInt(command.substring(11));
					if ((classId >= 88 && classId <= 118) || (classId >= 131 && classId <= 134) || classId == 136)
					{
						L2FastList<String> names = Olympiad.getInstance().getClassLeaderBoard(classId);
						reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "olympiad_ranking.htm");

						int index = 1;
						for (String name : names)
						{
							reply.replace("%place"+index+"%", String.valueOf(index));
							reply.replace("%rank"+index+"%", name);
							index++;
							if (index > 10)
								break;
						}
						for (; index <= 10; index++)
						{
							reply.replace("%place"+index+"%", "");
							reply.replace("%rank"+index+"%", "");
						}

						reply.replace("%objectId%", String.valueOf(getObjectId()));
						player.sendPacket(reply);
					}
					break;
                case 3:
                	int id = Integer.parseInt(command.substring(11));
                	Olympiad.addSpectator(id, player, true);
                	break;
                case 4:
                	player.sendPacket(new ExHeroList());
                	break;
                default:
                	_logOlymp.warning("Olympiad System: Couldnt send packet for request " + val);
                break;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void showChatWindow(L2PcInstance player, int val, String suffix)
	{
		String filename = Olympiad.OLYMPIAD_HTML_PATH;

		filename += "noble_desc" + val;
		filename += (suffix != null)? suffix + ".htm" : ".htm";

		if (filename.equals(Olympiad.OLYMPIAD_HTML_PATH + "noble_desc0.htm"))
			filename = Olympiad.OLYMPIAD_HTML_PATH + "noble_main.htm";

		showChatWindow(player, filename);
	}
}
