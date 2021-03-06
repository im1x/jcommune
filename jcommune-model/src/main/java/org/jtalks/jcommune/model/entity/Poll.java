/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jtalks.jcommune.model.entity;

import com.google.common.base.Joiner;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.validation.annotations.ValidPoll;
import org.jtalks.jcommune.model.validation.validators.PollValidator;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.forEach;
import static ch.lambdaj.Lambda.sumFrom;

/**
 * Represents the poll of the topic. Contains the list of related {@link PollItem}.
 * Poll may be either "single type" or "multiple type" also topic may have an end date.
 * Poll is tied to the life cycle of the topic({@link Topic}).
 *
 * @author Anuar Nurmakanov
 */
@ValidPoll(pollTitle = "title", pollItems = "pollItemsValue", endingDate = "endingDateValue")
public class Poll extends Entity {

    private static final DateTimeFormatter FORMAT = DateTimeFormat.forPattern("dd-MM-yyyy");
    private static final String SEPARATOR = System.getProperty("line.separator");
    
    @Size(min = Poll.MIN_TITLE_LENGTH, max = Poll.MAX_TITLE_LENGTH)
    private String title;
    private boolean multipleAnswer;
    private String endingDateValue;
    private DateTime endingDate;
    private String pollItemsValue;
    private List<PollItem> pollItems = new ArrayList<PollItem>();
    private Topic topic;

    public static final int MIN_TITLE_LENGTH = 3;
    public static final int MAX_TITLE_LENGTH = 120;
    public static final int MIN_ITEMS_NUMBER = 2;
    public static final int MAX_ITEMS_NUMBER = 50;

    /**
     * Used only by Hibernate.
     */
    public Poll() {
    }

    /**
     * Creates the Poll instance with required fields.
     * Poll is "single answer type" by default.
     *
     * @param title the poll title
     */
    public Poll(String title) {
        this.title = title;
    }

    /**
     * Get the poll title.
     *
     * @return the poll title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the the poll title.
     *
     * @param title the poll title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Poll may be either "single answer type" or "multiple answer type".
     *
     * @return <tt>false</tt> if the poll is "single answer type",
     *         <tt>true</tt> if the poll is "multiple answer type"
     */
    public boolean isMultipleAnswer() {
        return multipleAnswer;
    }

    /**
     * Set the poll type. Poll may be either "single answer type"
     * or "multiple answer type".
     *
     * @param multipleAnswer <tt>false</tt> if the poll is "single answer type",
     *                       <tt>true</tt> if the poll is "multiple answer type"
     */
    public void setMultipleAnswer(boolean multipleAnswer) {
        this.multipleAnswer = multipleAnswer;
    }

    /**
     * Get the poll ending date.
     *
     * @return the poll ending date
     */
    public DateTime getEndingDate() {
        return endingDate;
    }

    /**
     * Set the poll ending date.
     *
     * @param endingDate the poll ending date
     */
    public void setEndingDate(DateTime endingDate) {
        this.endingDate = endingDate;
        if (endingDate != null) {
            this.endingDateValue= FORMAT.print(endingDate);
        }
    }

    /**
     * @return poll ending date in string representation
     */
    public String getEndingDateValue() {
        return endingDateValue;
    }

    /**
     * Set string representation of poll ending date.
     * Parse this string and set ending date.
     *
     * @param endingDateValue poll ending date in string representation
     */
    public void setEndingDateValue(String endingDateValue) {
        this.endingDateValue = endingDateValue;
        if (endingDateValue != null) {
            setEndingDate(FORMAT.parseDateTime(endingDateValue));
        }
    }

    /**
     * @return poll options in string representation.
     */
    public String getPollItemsValue() {
        return pollItemsValue;
    }

    /**
     * Set string representation of poll options.
     * Parse this string and fill poll items list.
     *
     * @param pollItemsValue poll options in string representation
     */
    public void setPollItemsValue(String pollItemsValue) {
        this.pollItemsValue = pollItemsValue;
        if (StringUtils.isNotBlank(pollItemsValue)) {
            this.setPollItems(PollValidator.parseItems(pollItemsValue));
        }
    }

    /**
     * Get the list of poll options.
     *
     * @return the list of poll options
     */
    public List<PollItem> getPollItems() {
        return pollItems;
    }

    /**
     * Set the list of poll options.
     *
     * @param pollItems the list of poll options
     */
    public void setPollItems(List<PollItem> pollItems) {
        this.pollItems = pollItems;
        pollItemsValue = Joiner.on(SEPARATOR).join(pollItems);
    }

    /**
     * Get the topic that contains this poll.
     *
     * @return the topic that contains this poll
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * Get the topic that contains this poll.
     *
     * @param topic the topic that contains this poll
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }


    /**
     * Add the poll options to this poll.
     *
     * @param options the poll option
     */
    public void addPollOptions(PollItem... options) {
        addPollOptions(Arrays.asList(options));
    }

    /**
     * Add the list of poll options to this poll.
     *
     * @param options the list of poll options to this poll
     */
    public void addPollOptions(List<PollItem> options) {
        pollItems.addAll(options);
        forEach(options).setPoll(this);
    }

    /**
     * Counts the total count of votes in the poll.
     *
     * @return the total count of votes in the poll
     */
    public int getTotalVotesCount() {
        return sumFrom(pollItems, PollItem.class).getVotesCount();
    }

    /**
     * Evaluates the current activity of poll.
     *
     * @return <tt>true</tt>  if the poll is active
     *         <tt>false</tt>  if the poll is inactive
     */
    public boolean isActive() {
        return endingDate == null || endingDate.isAfterNow();
    }

    /**
     * Determines a existence the poll in the topic.
     *
     * @return <tt>true</tt>  if the poll exists
     *         <tt>false</tt>  if the poll doesn't exist
     */
    public boolean isHasPoll() {
        return StringUtils.isNotBlank(getTitle()) &&
                StringUtils.isNotBlank(getPollItemsValue());
    }

}
