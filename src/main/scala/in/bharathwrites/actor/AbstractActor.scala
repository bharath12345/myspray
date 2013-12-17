package in.bharathwrites.actor

import java.sql.SQLException
import in.bharathwrites.domain.{FailureType, Failure}

trait AbstractActor {

  protected def databaseError(e: SQLException) =
    Failure("%d: %s".format(e.getErrorCode, e.getMessage), FailureType.DatabaseFailure)

  protected def notFoundError(blogId: Long) =
    Failure("Blog with id=%d does not exist".format(blogId), FailureType.NotFound)
}
