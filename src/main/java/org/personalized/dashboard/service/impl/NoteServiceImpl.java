package org.personalized.dashboard.service.impl;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.personalized.dashboard.dao.api.ActivityDao;
import org.personalized.dashboard.dao.api.NoteDao;
import org.personalized.dashboard.model.Activity;
import org.personalized.dashboard.model.ActivityType;
import org.personalized.dashboard.model.EntityType;
import org.personalized.dashboard.model.Note;
import org.personalized.dashboard.service.api.NoteService;
import org.personalized.dashboard.utils.generator.ActivityGenerator;
import org.personalized.dashboard.utils.auth.SessionManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sudan on 3/4/15.
 */
@Repository
public class NoteServiceImpl implements NoteService {

    private final NoteDao noteDao;
    private final SessionManager sessionManager;
    private final ActivityGenerator activityGenerator;
    private final ActivityDao activityDao;

    @Inject
    public NoteServiceImpl(NoteDao noteDao, SessionManager sessionManager, ActivityGenerator activityGenerator, ActivityDao activityDao){
        this.noteDao = noteDao;
        this.sessionManager = sessionManager;
        this.activityGenerator = activityGenerator;
        this.activityDao = activityDao;
    }

    @Override
    public String createNote(Note note) {
        String noteId = noteDao.create(note, sessionManager.getUserIdFromSession());
        Activity activity = activityGenerator.generate(ActivityType.CREATED, EntityType.NOTE, noteId, note.getTitle());
        activityDao.add(activity, sessionManager.getUserIdFromSession());
        return noteId;
    }

    @Override
    public Note getNote(String noteId) {
        return noteDao.get(noteId, sessionManager.getUserIdFromSession());
    }

    @Override
    public Long updateNote(Note note) {
        Long modifiedCount = noteDao.update(note, sessionManager.getUserIdFromSession());
        if(modifiedCount > 0) {
            Activity activity = activityGenerator.generate(ActivityType.UPDATED, EntityType.NOTE, note.getNoteId(), note.getTitle());
            activityDao.add(activity, sessionManager.getUserIdFromSession());
        }
        return modifiedCount;
    }

    @Override
    public void deleteNote(String noteId) {
        Long deletedCount = noteDao.delete(noteId, sessionManager.getUserIdFromSession());
        if(deletedCount > 0) {
            Activity activity = activityGenerator.generate(ActivityType.DELETED, EntityType.NOTE, noteId, StringUtils.EMPTY);
            activityDao.add(activity, sessionManager.getUserIdFromSession());
        }
    }

    @Override
    public Long countNotes() {
        return noteDao.count(sessionManager.getUserIdFromSession());
    }

    @Override
    public List<Note> fetchNotes(int limit, int offset) {
        return noteDao.get(limit, offset, sessionManager.getUserIdFromSession());
    }
}
