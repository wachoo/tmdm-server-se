package com.amalto.core.storage.task;

import org.talend.mdm.commmon.metadata.MetadataRepository;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.SaverSource;
import com.amalto.core.storage.Storage;

public class StagingConfiguration {
    private final Storage origin;
    private final MetadataRepository stagingRepository;
    private final MetadataRepository userRepository;
    private final SaverSource source;
    private final SaverSession.Committer committer;
    private final Storage destination;

    public StagingConfiguration(Storage origin, MetadataRepository stagingRepository, MetadataRepository userRepository, SaverSource source, SaverSession.Committer committer, Storage destination) {
        this.origin = origin;
        this.stagingRepository = stagingRepository;
        this.userRepository = userRepository;
        this.source = source;
        this.committer = committer;
        this.destination = destination;
    }

    public Storage getOrigin() {
        return origin;
    }

    public MetadataRepository getStagingRepository() {
        return stagingRepository;
    }

    public MetadataRepository getUserRepository() {
        return userRepository;
    }

    public SaverSource getSource() {
        return source;
    }

    public SaverSession.Committer getCommitter() {
        return committer;
    }

    public Storage getDestination() {
        return destination;
    }
}
