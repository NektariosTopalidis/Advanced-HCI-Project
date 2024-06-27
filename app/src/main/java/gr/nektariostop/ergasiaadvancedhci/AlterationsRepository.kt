package gr.nektariostop.ergasiaadvancedhci

import gr.nektariostop.ergasiaadvancedhci.data.Alteration
import gr.nektariostop.ergasiaadvancedhci.data.AlterationDAO
import kotlinx.coroutines.flow.Flow

class AlterationsRepository(
    private val alterationDAO: AlterationDAO
) {


    suspend fun addAlteration(alteration: Alteration): Long{
        return alterationDAO.addAlteration(alteration)
    }

    fun getAlterations(): Flow<List<Alteration>> = alterationDAO.getAllAlterations()

    suspend fun deleteAlteration(alteration: Alteration){
        alterationDAO.deleteAlteration(alteration)
    }
}