package com.skdirect.litho

import com.airbnb.epoxy.TypedEpoxyController
import com.skdirect.R2

class GamesListController : TypedEpoxyController<Container>() {
    override fun buildModels(container: Container?) {
        container?.genres?.forEach {
             /*header {
                R2.id(it.header.id)
                genre(it.genre)
                onHeaderExpanded { model, _, _, _ ->
                    container.onGenreExpanded(model.genre())
                }
            }
            if (it.genre.isExpanded) {
                it.games.forEach { game ->
                    onelineWithIcon {
                        R2.id(game.id)
                        game(game)
                    }
                }
            }*/
        }
    }
}