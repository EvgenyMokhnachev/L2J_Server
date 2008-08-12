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
package net.sf.l2j.gameserver.skills.effects;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.skills.Env;

/**
 * @author kombat
 */
final class EffectBestowSkill extends L2Effect
{
	public EffectBestowSkill(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}

	/** Notify started */
	@Override
	public void onStart()
	{
		L2Skill tempSkill = SkillTable.getInstance().getInfo
				(getSkill().getTriggeredId(), getSkill().getTriggeredLevel());
		if (tempSkill != null)
			getEffected().addSkill(tempSkill);
	}

	@Override
	public void onExit()
	{
		getEffected().removeSkill(getSkill().getTriggeredId());
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
