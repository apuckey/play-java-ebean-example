package repository;

import io.ebean.*;
import models.Computer;
import org.joda.time.DateTime;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class ComputerRepository {

	private final EbeanServer ebeanServer;
	private final DatabaseExecutionContext executionContext;

	@Inject
	public ComputerRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
		this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
		this.executionContext = executionContext;
	}

	/**
	 * Return a paged list of computer
	 *
	 * @param page     Page to display
	 * @param pageSize Number of computers per page
	 * @param sortBy   Computer property used for sorting
	 * @param order    Sort order (either or asc or desc)
	 * @param filter   Filter applied on the name column
	 */
	public CompletionStage<PagedList<Computer>> page(int page, int pageSize, String sortBy, String order, String filter) {

		DateTime firstDayOfNextMonth = DateTime.now()
				.withDayOfMonth(1)
				.plusMonths(1);

		Timestamp timestamp = new Timestamp(firstDayOfNextMonth.getMillis());

		return supplyAsync(() ->
				ebeanServer.find(Computer.class).where()
						.ilike("name", "%" + filter + "%")
						.orderBy(sortBy + " " + order)
						.fetch("company")

						// If these 2 are enabled so an asOf Query with history the error happens.
						.fetch("company.employees")
						.fetch("company.employees.uniforms")
						
						.asOf(timestamp)
						.setFirstRow(page * pageSize)
						.setMaxRows(pageSize)
						.findPagedList(), executionContext);
	}

	public CompletionStage<Optional<Computer>> lookup(Long id) {
		return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(Computer.class).setId(id).findOne()), executionContext);
	}

	public CompletionStage<Optional<Long>> update(Long id, Computer newComputerData) {
		return supplyAsync(() -> {
			Transaction txn = ebeanServer.beginTransaction();
			Optional<Long> value = Optional.empty();
			try {
				Computer savedComputer = ebeanServer.find(Computer.class).setId(id).findOne();
				if (savedComputer != null) {
					savedComputer.company = newComputerData.company;
					savedComputer.discontinued = newComputerData.discontinued;
					savedComputer.introduced = newComputerData.introduced;
					savedComputer.name = newComputerData.name;

					savedComputer.update();
					txn.commit();
					value = Optional.of(id);
				}
			} finally {
				txn.end();
			}
			return value;
		}, executionContext);
	}

	public CompletionStage<Optional<Long>> delete(Long id) {
		return supplyAsync(() -> {
			try {
				final Optional<Computer> computerOptional = Optional.ofNullable(ebeanServer.find(Computer.class).setId(id).findOne());
				computerOptional.ifPresent(Model::delete);
				return computerOptional.map(c -> c.id);
			} catch (Exception e) {
				return Optional.empty();
			}
		}, executionContext);
	}

	public CompletionStage<Long> insert(Computer computer) {
		return supplyAsync(() -> {
			computer.id = System.currentTimeMillis(); // not ideal, but it works
			ebeanServer.insert(computer);
			return computer.id;
		}, executionContext);
	}
}
