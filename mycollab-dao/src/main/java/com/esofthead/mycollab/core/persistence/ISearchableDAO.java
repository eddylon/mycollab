/**
 * This file is part of mycollab-dao.
 *
 * mycollab-dao is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-dao is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-dao.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This file is part of mycollab-core.
 *
 * mycollab-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.core.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.esofthead.mycollab.core.arguments.SearchCriteria;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 * 
 * @param <S>
 */
public interface ISearchableDAO<S extends SearchCriteria> {

	/**
	 * 
	 * @param criteria
	 * @return
	 */
	int getTotalCount(@Param("searchCriteria") S criteria);

	/**
	 * 
	 * @param criteria
	 * @param rowBounds
	 * @return
	 */
	List findPagableListByCriteria(@Param("searchCriteria") S criteria,
			RowBounds rowBounds);

	/**
	 * 
	 * @param criteria
	 * @return
	 */
	Integer getNextItemKey(@Param("searchCriteria") S criteria);

	/**
	 * 
	 * @param criteria
	 * @return
	 */
	Integer getPreviousItemKey(@Param("searchCriteria") S criteria);

	/**
	 * 
	 * @param criteria
	 */
	void removeByCriteria(@Param("searchCriteria") S criteria);
}
