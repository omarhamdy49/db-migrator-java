# Default database engine
ENGINE ?= postgres

# Run checks on DB engines
check:
	./gradlew run --args="check --engine=$(ENGINE)"

# Run migrations on DB engines
migrate:
	./gradlew run --args="migrate --engine=$(ENGINE)"

# Rollback using a tag (must set TAG)
rollback:
ifndef TAG
	$(error TAG is not set. Use 'make rollback TAG=your-tag')
endif
	./gradlew run --args="rollback-tag --tag=$(TAG) --engine=$(ENGINE)"

# Make table (must set NAME)
new:
ifndef NAME
	$(error NAME is not set. Use 'make new NAME=table_name')
endif
	./gradlew run --args="make --name=$(NAME) --engine=$(ENGINE)"
