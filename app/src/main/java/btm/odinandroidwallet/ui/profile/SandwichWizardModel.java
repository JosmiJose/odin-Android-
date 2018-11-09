package btm.odinandroidwallet.ui.profile;

import android.app.Activity;
import android.content.Context;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.ui.profile.model.AbstractWizardModel;
import btm.odinandroidwallet.ui.profile.model.CustomerInfoPage;
import btm.odinandroidwallet.ui.profile.model.InvestmentDetailsPage;
import btm.odinandroidwallet.ui.profile.model.PageList;
import btm.odinandroidwallet.ui.profile.model.ProofOfIdentityPage;
import btm.odinandroidwallet.ui.profile.model.ResdentialPage;

public class SandwichWizardModel extends AbstractWizardModel {
    public SandwichWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
               new CustomerInfoPage(this, mContext.getString(R.string.personal_info),mContext)
                        .setRequired(true),
                new ResdentialPage(this, mContext.getString(R.string.residential_address),mContext)
                        .setRequired(true),
                new InvestmentDetailsPage(this, mContext.getString(R.string.investment_details),mContext)
                        .setRequired(true),
                new ProofOfIdentityPage(this, mContext.getString(R.string.proof_of_identity),mContext)
                        .setRequired(true)
        );
    }
}