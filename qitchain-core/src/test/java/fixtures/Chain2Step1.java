/*
 * Copyright 2012 QitSoft LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fixtures;

import com.qitsoft.qitchain.ChainStepExecutor;
import com.qitsoft.qitchain.ChainStorage;
import com.qitsoft.qitchain.annotation.Chain;
import com.qitsoft.qitchain.annotation.ChainStep;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
@Chain("chain2")
@ChainStep
public class Chain2Step1 implements ChainStepExecutor {

    public Status execute(ChainStorage storage) {
        return null;
    }
    
}
