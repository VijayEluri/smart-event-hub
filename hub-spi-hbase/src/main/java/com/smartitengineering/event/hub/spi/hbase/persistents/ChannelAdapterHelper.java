/*
It is a application for event distribution to event n-consumers with m-sources.
Copyright (C) 2010 "Imran M Yousuf <imran@smartitengineering.com>"

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or any later
version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.smartitengineering.event.hub.spi.hbase.persistents;

import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.Filter.SupportedMimeType;
import com.smartitengineering.event.hub.api.impl.APIFactory;
import com.smartitengineering.event.hub.api.impl.APIFactory.ChannelBuilder;
import com.smartitengineering.util.bean.adapter.AbstractAdapterHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
public class ChannelAdapterHelper
    extends AbstractAdapterHelper<Channel, PersistentChannel> {

  protected transient final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  protected PersistentChannel newTInstance() {
    return new PersistentChannel();
  }

  @Override
  protected void mergeFromF2T(Channel fromBean,
                              PersistentChannel toBean) {
    if (fromBean.getPosition() > 0) {
      toBean.setId(Long.MAX_VALUE - fromBean.getPosition());
    }
    toBean.setDescription(fromBean.getDescription());
    toBean.setAuthToken(fromBean.getAuthToken());
    toBean.setAutoExpiryDateTime(fromBean.getAutoExpiryDateTime());
    if (StringUtils.isNotBlank(fromBean.getName())) {
      toBean.setName(fromBean.getName().toLowerCase());
    }
    if (fromBean.getFilter() != null) {
      toBean.setFilterType(fromBean.getFilter().getMimeType().name());
      toBean.setScript(fromBean.getFilter().getFilterScript());
    }
  }

  @Override
  protected Channel convertFromT2F(PersistentChannel toBean) {
    final ChannelBuilder builder = APIFactory.getChannelBuilder(toBean.getName()).description(toBean.getDescription()).
        authToken(toBean.getAuthToken()).autoExpiryDateTime(toBean.getAutoExpiryDateTime()).creationDateTime(toBean.
        getCreationDateTime()).lastModifiedDate(toBean.getLastModifiedDateTime());
    int pos = -1;
    if (logger.isDebugEnabled()) {
      logger.debug("Channel ID " + toBean.getId());
    }
    if (toBean.getId() != null) {
      pos = NumberUtils.toInt(new Long(Long.MAX_VALUE - toBean.getId().longValue()).toString());
    }
    builder.position(pos);
    if (StringUtils.isNotBlank(toBean.getFilterType())) {
      builder.filter(
          APIFactory.getFilter(SupportedMimeType.valueOf(toBean.getFilterType()),
                               toBean.getScript()));
    }
    return builder.build();
  }
}
