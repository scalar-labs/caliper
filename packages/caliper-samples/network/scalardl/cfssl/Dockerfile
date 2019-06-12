FROM cfssl/cfssl:1.3.2

# Install goose
RUN go get bitbucket.org/liamstask/goose/cmd/goose

WORKDIR /cfssl

# Copy config JSON files
COPY *.json ./

ENV DATADIR /cfssl/data

RUN mkdir $DATADIR
VOLUME $DATADIR

COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]

EXPOSE 8888 8889
